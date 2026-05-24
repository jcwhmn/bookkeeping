本文档详细阐述 ezBookkeeping 后端 API 的统一响应格式设计、错误码体系架构，以及前后端如何协同处理 API 响应。这些规范确保了系统各组件间的通信一致性，是构建可靠 API 服务的基础。

## 统一响应格式设计

ezBookkeeping 采用了**信封模式（Envelope Pattern）**作为所有 REST API 响应的标准封装格式。这种设计将业务数据与元数据分离，使客户端能够以统一的方式解析成功和失败响应。

### 响应结构

所有 API 响应均遵循以下 JSON 结构：

```json
// 成功响应
{
  "success": true,
  "result": { /* 业务数据 */ }
}

// 错误响应
{
  "success": false,
  "errorCode": 2001,
  "errorMessage": "Authentication failed",
  "path": "/api/authorize.json"
}
```

从 `openapi.yaml` 的组件定义中可以看到响应模式的具体实现：

```yaml
# 成功响应模式
SuccessResponse:
  type: object
  properties:
    success:
      type: boolean
      enum: [true]
    result:
      description: Response data (varies by endpoint)

# 错误响应模式
ErrorResponse:
  type: object
  properties:
    success:
      type: boolean
      enum: [false]
    errorCode:
      type: integer
      format: int32
      description: Error code (see error code format in API description)
    errorMessage:
      type: string
      description: Human-readable error message
    path:
      type: string
      description: Request path
```

Sources: [openapi.yaml](openapi.yaml#L2590-L2620)

### 后端实现

后端通过 `ApiResponse` 记录类实现这一响应格式。该类使用 Java Record 特性提供了简洁的不可变数据载体定义：

```java
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    @JsonProperty("success") boolean isSuccess,
    T result,
    Integer errorCode,
    String errorMessage
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null);
    }
    
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, null, null, null);
    }
    
    public static <T> ApiResponse<T> error(int errorCode, String errorMessage) {
        return new ApiResponse<>(false, null, errorCode, errorMessage);
    }
}
```

Sources: [ApiResponse.java](backend/src/main/java/com/bookkeeping/common/ApiResponse.java#L1-L41)

### 前端类型定义

前端 TypeScript 定义了对应的响应接口，使 API 响应能够被类型化处理：

```typescript
interface ApiResponse<T> {
  success: boolean
  result: T
  errorCode?: number
  errorMessage?: string
}
```

Sources: [useApi.ts](frontend/composables/useApi.ts#L2-L7)

### 核心设计原则

**分离关注点**：成功时返回 `result`，错误时返回 `errorCode` 和 `errorMessage`，两者互斥且非空字段最小化。

**HTTP 状态码策略**：项目采用了一个值得注意的设计——业务错误始终返回 HTTP 200 状态码，错误信息通过响应体的 `success: false` 表达。这是 RESTful API 的常见实践，将 HTTP 状态码保留给传输层异常，而业务层的成功/失败由响应体表达。

Sources: [openapi.yaml](openapi.yaml#L1-L30)

## 错误码体系架构

错误码采用**层级式编码方案**，便于错误分类、定位和处理。

### 编码公式

错误码遵循以下计算公式：

```
错误码 = category × 100000 + subCategory × 1000 + index
```

这种结构化编码使得从错误码即可推断错误类型和来源。

### 错误码分类表

| 分类 | 范围 | 说明 |
|------|------|------|
| 系统错误 | 1xxxxx | 服务器内部错误、配置问题 |
| 业务错误 | 2xxxxx | 业务逻辑验证失败 |
| 子类别 | *001-*999 | 各功能模块具体错误 |

Sources: [openapi.yaml](openapi.yaml#L1-L30)

### 业务错误码详细定义

后端 `ResultCode` 枚举定义了完整的业务错误码体系：

```java
public enum ResultCode {
    // 通用错误 (1xxx)
    SUCCESS(0, "Success"),
    BAD_REQUEST(1001, "Bad request"),
    UNAUTHORIZED(1002, "Unauthorized"),
    FORBIDDEN(1003, "Forbidden"),
    NOT_FOUND(1004, "Resource not found"),
    INTERNAL_ERROR(1005, "Internal server error"),
    VALIDATION_ERROR(1006, "Validation error"),
    
    // 认证错误 (2xxx)
    AUTHENTICATION_FAILED(2001, "Authentication failed"),
    AUTH_TOKEN_EXPIRED(2002, "Token has expired"),
    AUTH_TOKEN_INVALID(2003, "Invalid token"),
    
    // 用户错误 (3xxx)
    USER_NOT_FOUND(3001, "User not found"),
    USER_ALREADY_EXISTS(3002, "User already exists"),
    
    // 账户错误 (4xxx)
    ACCOUNT_NOT_FOUND(4001, "Account not found"),
    ACCOUNT_ALREADY_EXISTS(4002, "Account already exists"),
    
    // 分类错误 (5xxx)
    CATEGORY_NOT_FOUND(5001, "Category not found"),
    
    // 交易错误 (6xxx)
    TRANSACTION_NOT_FOUND(6001, "Transaction not found"),
    TRANSACTION_INSUFFICIENT_BALANCE(6002, "Insufficient balance");
}
```

Sources: [ResultCode.java](backend/src/main/java/com/bookkeeping/common/ResultCode.java#L1-L66)

### 错误码子类别映射

| 子类别 | 范围前缀 | 涵盖实体 |
|--------|----------|----------|
| Global | 0xxx | 通用的基础错误 |
| User | 1xxx | 用户相关操作 |
| Token | 2xxx | JWT/会话令牌 |
| 2FA | 3xxx | 双因素认证 |
| Account | 4xxx | 账户管理 |
| Transaction | 5xxx | 交易记录 |
| Category | 6xxx | 分类体系 |
| Tag | 7xxx | 标签管理 |
| Data | 8xxx | 数据操作 |
| MCP | 14xxx | 模型上下文协议 |
| LLM | 15xxx | AI 功能 |

Sources: [openapi.yaml](openapi.yaml#L1-L30)

## 全局异常处理机制

`GlobalExceptionHandler` 是后端异常处理的中央调度器，负责将各种异常转换为统一格式的 API 响应。

### 处理器架构

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        log.warn("Business exception: {} - {}", ex.getErrorCode(), ex.getErrorMessage());
        return ResponseEntity
            .status(HttpStatus.OK)  // 业务错误返回 200
            .body(ApiResponse.error(ex.getErrorCode(), ex.getErrorMessage()));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining(", "));
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.error(ResultCode.VALIDATION_ERROR.getCode(), message));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)  // 系统错误返回 500
            .body(ApiResponse.error(ResultCode.INTERNAL_ERROR.getCode(), "An unexpected error occurred"));
    }
}
```

Sources: [GlobalExceptionHandler.java](backend/src/main/java/com/bookkeeping/exception/GlobalExceptionHandler.java#L1-L75)

### 异常类型处理策略

| 异常类型 | HTTP 状态码 | 响应 `success` | 典型场景 |
|----------|-------------|----------------|----------|
| `BusinessException` | 200 | false | 业务验证失败 |
| `MethodArgumentNotValidException` | 200 | false | 请求参数校验失败 |
| `IllegalArgumentException` | 200 | false | 非法参数 |
| `Exception` | 500 | false | 未预期的系统错误 |

Sources: [GlobalExceptionHandler.java](backend/src/main/java/com/bookkeeping/exception/GlobalExceptionHandler.java#L20-L70)

### 业务异常定义

`BusinessException` 支持多种构造方式，便于在不同场景下抛出：

```java
public class BusinessException extends RuntimeException {
    private final int errorCode;
    private final String errorMessage;
    
    public BusinessException(ResultCode resultCode) { ... }
    public BusinessException(ResultCode resultCode, String message) { ... }
    public BusinessException(int errorCode, String errorMessage) { ... }
}
```

Sources: [BusinessException.java](backend/src/main/java/com/bookkeeping/exception/BusinessException.java#L1-L38)

## 前端响应处理

前端 `useApi` 组合式函数封装了与后端的通信逻辑，统一处理响应和错误。

### 请求封装

```typescript
async function request<T>(method: string, path: string, body?: unknown): Promise<T> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...getAuthHeaders(),
  }

  const options: RequestInit = { method, headers }
  if (body) options.body = JSON.stringify(body)

  const res = await fetch(`${API_BASE}${path}`, options)
  const data = await res.json() as ApiResponse<T>

  if (!data.success) {
    throw createError({
      statusCode: data.errorCode || res.status,
      statusMessage: data.errorMessage || 'Unknown error',
    })
  }

  return data.result
}
```

Sources: [useApi.ts](frontend/composables/useApi.ts#L14-L37)

### 错误处理流程

前端采用**异常驱动**的错误处理模式：

1. 检测 `success` 字段为 `false`
2. 抛出 Nuxt 的 `createError`
3. 由全局错误处理中间件或页面 `error.vue` 展示

### 认证中间件

前端路由中间件确保受保护路由的访问权限：

```typescript
export default defineNuxtRouteMiddleware(() => {
  const token = useCookie<string>('token').value
  if (!token) {
    return navigateTo('/login')
  }
})
```

Sources: [auth.ts](frontend/middleware/auth.ts#L1-L8)

## API 端点响应示例

### 认证成功

```json
POST /api/authorize.json
{
  "success": true,
  "result": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "need2FA": false,
    "user": {
      "username": "john",
      "email": "john@example.com",
      "defaultCurrency": "USD"
    },
    "applicationCloudSettings": []
  }
}
```

### 认证失败

```json
POST /api/authorize.json
{
  "success": false,
  "errorCode": 2001,
  "errorMessage": "Authentication failed",
  "path": "/api/authorize.json"
}
```

### 资源未找到

```json
GET /api/v1/accounts/9999
{
  "success": false,
  "errorCode": 4001,
  "errorMessage": "Account not found",
  "path": "/api/v1/accounts/9999"
}
```

Sources: [openapi.yaml](openapi.yaml#L130-L200)

## 控制器响应模式

控制器层直接返回 `ApiResponse` 对象，由框架自动序列化：

```java
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    @GetMapping
    public ApiResponse<List<AccountDto>> listAccounts() {
        return ApiResponse.success(accountService.getCurrentUserAccounts());
    }

    @PostMapping
    public ApiResponse<AccountDto> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return ApiResponse.success(accountService.createAccount(request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ApiResponse.success(null);
    }
}
```

Sources: [AccountController.java](backend/src/main/java/com/bookkeeping/core/account/AccountController.java#L1-L58)

## 相关文档

- [认证机制 - JWT 令牌与安全配置](6-ren-zheng-ji-zhi-jwt-ling-pai-yu-an-quan-pei-zhi) — 了解 JWT 认证与令牌的完整生命周期
- [系统架构 - Spring Boot + Nuxt 4 全栈设计](3-xi-tong-jia-gou-spring-boot-nuxt-4-quan-zhan-she-ji) — 探索前后端架构全景