预算管理模块为用户提供按分类设置月度支出上限的能力，并通过实时追踪当前消费进度，帮助用户理性控制开支。该模块采用前后端分离架构，后端基于 Spring Boot JPA 实现数据持久化，前端使用 Vue 3 Composition API 构建响应式界面。

## 数据模型设计

预算系统以 `budgets` 表为核心，采用用户-分类-月份三维索引结构实现精确的预算分配。

```sql
CREATE TABLE IF NOT EXISTS budgets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    amount BIGINT NOT NULL,          -- 单位：分（cents）
    year INT NOT NULL,
    month INT NOT NULL,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT
);
```

实体类 `Budget.java` 使用 Lombok 注解简化代码，通过 `@Builder` 模式支持流式构建，`@NoArgsConstructor(access = AccessLevel.PROTECTED)` 确保 JPA 代理正常运作。金额字段以 **分为单位存储**，避免浮点数精度问题。

Sources: [V5__budgets.sql](backend/src/main/resources/db/migration/V5__budgets.sql#L1-L18), [Budget.java](backend/src/main/java/com/bookkeeping/core/budget/Budget.java#L1-L39)

## 服务层业务逻辑

`BudgetService` 封装了预算管理的核心业务规则，包含创建、更新、删除以及消费追踪功能。

### 预算创建与重复校验

创建预算时，系统通过 `BudgetRepository.findByUserIdAndCategoryIdAndYearAndMonth()` 检查是否存在重复记录。若同一用户在某月份已为某分类设置预算，则抛出验证错误：

```java
if (budgetRepository.findByUserIdAndCategoryIdAndYearAndMonth(
        userId, request.categoryId(), request.year(), request.month()).isPresent()) {
    throw new BusinessException(ResultCode.VALIDATION_ERROR, 
        "Budget already exists for this category and month");
}
```

这种设计确保每个用户在每个月份的每个分类下最多只有一个预算限额。

Sources: [BudgetService.java](backend/src/main/java/com/bookkeeping/core/budget/BudgetService.java#L48-L54)

### 消费进度计算

`toDtoWithSpent()` 方法是预算追踪的核心，它需要完成两项计算：获取分类名称和统计已消费金额。

```java
LocalDate startDate = LocalDate.of(budget.getYear(), budget.getMonth(), 1);
LocalDate endDate = startDate.plusMonths(1);
long startTime = startDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();
long endTime = endDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond();

List<?> transactions = transactionRepository.findByUserIdAndMonth(userId, startTime, endTime);
long spent = transactions.stream()
        .filter(tx -> tx instanceof Transaction)
        .map(tx -> (Transaction) tx)
        .filter(tx -> tx.getCategoryId() != null && 
                    tx.getCategoryId().equals(budget.getCategoryId()) && 
                    tx.getTransactionType() == 3)  // 3 = Expense
        .mapToLong(Transaction::getAmount)
        .sum();

double percentUsed = budget.getAmount() > 0 ? (spent * 100.0 / budget.getAmount()) : 0;
```

时间范围采用 Unix 时间戳计算，利用 `LocalDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()` 确保时区正确。消费统计仅包含交易类型为 **支出（type=3）** 且分类匹配的记录，最终返回已消费金额和占用百分比。

Sources: [BudgetService.java](backend/src/main/java/com/bookkeeping/core/budget/BudgetService.java#L93-L113)

## REST API 接口规范

预算控制器暴露四个核心端点，采用统一响应格式 `ApiResponse<T>` 包装返回值。

| 方法 | 路径 | 功能 | 请求体 |
|------|------|------|--------|
| GET | `/api/v1/budgets?year=&month=` | 获取指定月份所有预算 | - |
| POST | `/api/v1/budgets` | 创建新预算 | `CreateBudgetRequest` |
| PUT | `/api/v1/budgets/{id}` | 更新预算金额 | `UpdateBudgetRequest` |
| DELETE | `/api/v1/budgets/{id}` | 删除预算 | - |

`BudgetDto` 作为响应数据类型，包含预算基本信息与实时消费数据：

```java
public record BudgetDto(
    Long id,
    Long categoryId,
    String categoryName,     // 从分类服务获取
    Long amount,             // 预算上限（分）
    Integer year,
    Integer month,
    Long spent,              // 已消费金额（分）
    Double percentUsed       // 消费百分比
) {}
```

创建请求 `CreateBudgetRequest` 包含分类 ID、金额、年份和月份四项必填字段；更新请求 `UpdateBudgetRequest` 仅允许修改金额，不支持修改分类和月份以保持数据一致性。

Sources: [BudgetController.java](backend/src/main/java/com/bookkeeping/core/budget/BudgetController.java#L1-L49), [BudgetDto.java](backend/src/main/java/com/bookkeeping/core/budget/BudgetDto.java#L1-L12), [CreateBudgetRequest.java](backend/src/main/java/com/bookkeeping/core/budget/CreateBudgetRequest.java#L1-L11)

## 前端界面实现

`budgets.vue` 页面采用 Vuetify 3 组件库构建，核心功能包括月度切换、预算卡片展示和编辑对话框。

### 月度选择器

通过 `prevMonth()` 和 `nextMonth()` 函数实现月份导航，跨年时会自动切换年份：

```typescript
function prevMonth() {
  if (selectedMonth.value === 1) {
    selectedMonth.value = 12
    selectedYear.value--
  } else {
    selectedMonth.value--
  }
  fetchBudgets()
}
```

月份标签使用 `toLocaleDateString('en-US', { month: 'long', year: 'numeric' })` 格式化显示。

### 预算卡片布局

采用 Vuetify 的 Grid 系统 `v-row` 和 `v-col` 实现响应式布局，每张卡片展示分类名、预算金额、消费进度条和剩余金额：

```vue
<v-progress-linear
  :model-value="Math.min(budget.percentUsed, 100)"
  :color="budgetColor(budget.percentUsed)"
  height="12"
  rounded
/>
```

进度条颜色根据消费比例动态调整：
- **绿色 (success)**：消费 < 80%
- **橙色 (warning)**：消费 80% ~ 99%
- **红色 (error)**：消费 ≥ 100%（显示 "Over budget!" 警告）

### 编辑流程

创建和编辑共用同一个对话框，通过 `editingBudget` 状态区分：
- **创建模式**：`editingBudget = null`，分类可选择
- **编辑模式**：`editingBudget` 指向已有预算，分类字段禁用

金额输入以元为单位，保存时乘以 100 转换为分存储：

```typescript
async function save() {
  const amount = Math.round(parseFloat(amountStr.value) * 100)
  if (editingBudget.value) {
    await api.put(`/budgets/${editingBudget.value.id}`, { amount })
  } else {
    await api.post('/budgets', {
      categoryId: form.categoryId,
      amount,
      year: selectedYear.value,
      month: selectedMonth.value,
    })
  }
}
```

Sources: [budgets.vue](frontend/pages/budgets.vue#L1-L270)

## 数据流架构

以下 Mermaid 图展示了预算管理模块的完整数据流转路径：

```mermaid
flowchart TB
    subgraph Frontend
        B[Budgets Page] --> C[Month Selector]
        B --> D[Budget Cards]
        B --> E[Edit Dialog]
    end
    
    subgraph Backend["API Layer"]
        F[BudgetController] --> G[BudgetService]
    end
    
    subgraph Persistence["Data Layer"]
        H[BudgetRepository] --> I[(budgets)]
        G --> J[CategoryService]
        G --> K[TransactionRepository]
        K --> L[(transactions)]
    end
    
    C -->|GET /budgets?year=&month=| F
    E -->|POST /budgets| F
    E -->|PUT /budgets/{id}| F
    
    F -->|Query budgets| H
    H -->|fetchBudgets| I
    
    G -->|getCategoryName| J
    G -->|calculateSpent| K
    K -->|findByUserIdAndMonth| L
```

数据获取时，`BudgetService.getBudgets()` 首先查询用户在该月份的所有预算记录，然后为每条记录注入分类名称和消费统计。`TransactionRepository.findByUserIdAndMonth()` 通过自定义 JPQL 查询，按时间范围筛选交易数据。

Sources: [BudgetRepository.java](backend/src/main/java/com/bookkeeping/core/budget/BudgetRepository.java#L1-L21), [TransactionRepository.java](backend/src/main/java/com/bookkeeping/core/transaction/TransactionRepository.java#L18-L24)

## 安全性设计

所有预算操作通过 `SecurityUtils.requireCurrentUser()` 获取当前登录用户 ID，确保数据隔离：

```java
Long userId = securityUtils.requireCurrentUser().getId();
Budget budget = budgetRepository.findByIdAndUserId(id, userId)
        .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "Budget not found"));
```

查询时强制附加用户 ID 条件，防止跨用户数据访问。删除操作同样验证所有权，确保用户只能管理自己的预算。

---

**相关文档**：
- [分类管理 - 收入支出分类体系](11-fen-lei-guan-li-shou-ru-zhi-chu-fen-lei-ti-xi) — 预算分类数据来源
- [交易管理 - 交易类型与金额处理](10-jiao-yi-guan-li-jiao-yi-lei-xing-yu-jin-e-chu-li) — 消费数据统计基础
- [认证机制 - JWT 令牌与安全配置](6-ren-zheng-ji-zhi-jwt-ling-pai-yu-an-quan-pei-zhi) — 用户身份验证机制