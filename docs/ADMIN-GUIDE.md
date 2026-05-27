# Bookkeeping 管理员指南

> 本文档是 Java 版 Bookkeeping 系统的运维和管理指南。
>
> **最后更新**: 2026-05-27
>
> **更新规则**: 新增功能后，必须同步更新本文档。

---

## 目录

1. [系统架构](#1-系统架构)
2. [环境要求](#2-环境要求)
3. [部署指南](#3-部署指南)
4. [数据库管理](#4-数据库管理)
5. [配置参考](#5-配置参考)
6. [监控与日志](#6-监控与日志)
7. [安全设置](#7-安全设置)
8. [备份与恢复](#8-备份与恢复)
9. [故障排除](#9-故障排除)
10. [开发指南](#10-开发指南)

---

## 1. 系统架构

### 1.1 技术栈

| 组件 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 4.0.6 |
| Java | OpenJDK | 25 |
| ORM | Spring Data JPA / Hibernate | 6.x |
| 数据库 | PostgreSQL | 17+ |
| 数据库迁移 | Flyway | 11.4.1 |
| 认证 | JWT (JJWT) | 0.12.6 |
| API 文档 | SpringDoc OpenAPI | 2.8.8 |
| 前端 | Nuxt 4 + Vue 3 + Vuetify 3 | latest |
| 缓存 | Caffeine | 3.x |

### 1.2 项目结构

```
bookkeeping/
├── backend/                    # Spring Boot 后端
│   ├── src/main/java/         # Java 源代码
│   │   ├── common/            # 公共组件 (BaseEntity, ApiResponse, enums)
│   │   ├── config/            # 配置类
│   │   ├── core/              # 核心业务模块 (account, category, transaction)
│   │   ├── exception/          # 异常处理
│   │   ├── infrastructure/    # 基础设施 (controller, config)
│   │   └── supporting/        # 支持模块 (auth, user, onboarding)
│   ├── src/main/resources/    # 配置和 SQL 迁移
│   │   ├── application.yml    # 主配置
│   │   └── db/migration/      # Flyway 迁移脚本
│   └── build.gradle.kts       # Gradle 构建配置
├── frontend/                   # Nuxt 前端 (开发中)
├── docs/                       # 文档
└── scripts/                    # 脚本
```

---

## 2. 环境要求

### 2.1 必需软件

| 软件 | 最低版本 | 推荐版本 |
|------|----------|----------|
| Java (OpenJDK) | 21 | 25 |
| PostgreSQL | 15 | 17+ |
| Docker | 24.0 | 最新 |
| Docker Compose | 2.20 | 最新 |

### 2.2 硬件要求

| 环境 | CPU | 内存 | 磁盘 |
|------|-----|------|------|
| 开发 | 2 核 | 4 GB | 10 GB |
| 生产 | 4 核 | 8 GB | 50 GB |

### 2.3 开发环境快速启动

```bash
# 1. 启动 PostgreSQL（创建 3 个数据库）
cd bookkeeping
./scripts/start-db.sh        # Linux/Mac
scripts\start-db.bat         # Windows

# 2. 运行后端
cd backend
./gradlew bootRun

# 3. 前端（待开发）
cd frontend
npm run dev
```

---

## 3. 部署指南

### 3.1 Docker 部署

#### 3.1.1 docker-compose.yml

```yaml
version: '3.8'

services:
  postgres:
    image: postgres:17
    environment:
      POSTGRES_USER: bookkeeping
      POSTGRES_PASSWORD: test123
      POSTGRES_DB: bookkeeping
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./scripts/init-databases.sql:/docker-entrypoint-initdb.d/init.sql
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U bookkeeping"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build: ./backend
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: prod
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/bookkeeping
      SPRING_DATASOURCE_USERNAME: bookkeeping
      SPRING_DATASOURCE_PASSWORD: test123
    depends_on:
      postgres:
        condition: service_healthy

volumes:
  postgres_data:
```

#### 3.1.2 启动命令

```bash
# 构建并启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

### 3.2 生产环境配置

创建 `application-prod.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bookkeeping
    username: ${DB_USER}
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false

server:
  port: 8080
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_PASSWORD}
```

### 3.3 构建生产 JAR

```bash
cd backend

# 清理并构建
./gradlew clean build -x test

# 运行
java -jar build/libs/bookkeeping-1.0.0.jar --spring.profiles.active=prod
```

---

## 4. 数据库管理

### 4.1 数据库结构

#### 4.1.1 核心表

| 表名 | 说明 |
|------|------|
| users | 用户表 |
| accounts | 账户表 |
| categories | 分类表 |
| transactions | 交易表 |
| tags | 标签表 |
| tag_groups | 标签组表 |
| budgets | 预算表 |
| exchange_rates | 汇率表 |
| transaction_templates | 交易模板表 |
| transaction_pictures | 交易图片表 |
| tokens | Token 表 |
| insights_explorers | 洞察分析器表 |

#### 4.1.2 用户表结构

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(32) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    salt VARCHAR(10) NOT NULL,
    nickname VARCHAR(64),
    avatar VARCHAR(255),
    default_currency VARCHAR(3) DEFAULT 'CNY',
    default_account_id BIGINT,
    language VARCHAR(10) DEFAULT 'zh-CN',
    first_day_of_week INTEGER DEFAULT 0,
    fiscal_year_start VARCHAR(10) DEFAULT '01-01',
    date_format_string VARCHAR(50) DEFAULT 'YYYY-MM-DD',
    transaction_edit_scope VARCHAR(20) DEFAULT 'unlimited',
    onboarding_completed BOOLEAN DEFAULT FALSE,
    disabled BOOLEAN DEFAULT FALSE,
    email_verified BOOLEAN DEFAULT FALSE,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL
);
```

#### 4.1.3 账户表结构

```sql
CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    name VARCHAR(100) NOT NULL,
    account_type INTEGER NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'CNY',
    icon VARCHAR(50),
    color VARCHAR(20),
    notes TEXT,
    sort_order INTEGER DEFAULT 0,
    hidden BOOLEAN DEFAULT FALSE,
    include_in_total BOOLEAN DEFAULT TRUE,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at BIGINT
);
```

#### 4.1.4 交易表结构

```sql
CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    account_id BIGINT NOT NULL REFERENCES accounts(id),
    target_account_id BIGINT REFERENCES accounts(id),
    category_id BIGINT REFERENCES categories(id),
    related_id BIGINT,
    type INTEGER NOT NULL,
    amount BIGINT NOT NULL,
    transaction_time BIGINT NOT NULL,
    notes TEXT,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    location_name VARCHAR(255),
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    deleted BOOLEAN DEFAULT FALSE,
    deleted_at BIGINT
);
```

### 4.2 Flyway 迁移

#### 4.2.1 迁移脚本位置

```
backend/src/main/resources/db/migration/
├── V1__init.sql
├── V2__accounts.sql
├── V3__categories_transactions.sql
├── V4__tags.sql
├── V5__budgets.sql
├── V6__account_enhancements.sql
├── V7__tag_enhancements.sql
├── V8__category_enhancements.sql
├── V9__user_profile_and_data_management.sql
└── V10__onboarding.sql
```

#### 4.2.2 创建新迁移

1. 在 `db/migration/` 目录创建新文件
2. 文件名格式: `V{N}__描述.sql`
3. 版本号必须递增

**示例**:
```sql
-- V11__new_feature.sql
CREATE TABLE IF NOT EXISTS new_feature (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    name VARCHAR(100) NOT NULL,
    created_at BIGINT NOT NULL
);
```

#### 4.2.3 迁移命令

```bash
# 手动运行迁移
./gradlew flywayMigrate

# 验证迁移状态
./gradlew flywayInfo

# 清理并重新迁移（危险！）
./gradlew flywayClean flywayMigrate
```

### 4.3 数据库连接

| 环境 | 数据库 | 用户 | 密码 |
|------|--------|------|------|
| 开发 | bookkeeping_dev | bookkeeping | test123 |
| 测试 | bookkeeping_test | bookkeeping | test123 |
| 生产 | bookkeeping | bookkeeping | (设置强密码) |

### 4.4 初始化数据

#### 4.4.1 测试用户

首次启动时，系统会自动创建测试用户：

```sql
INSERT INTO users (username, email, password, salt, default_currency, created_at, updated_at)
VALUES ('demo', 'demo@example.com', '$2a$10$...', '...', 'CNY', unix_now(), unix_now());
```

**默认测试账号**:
- 用户名: `demo`
- 密码: `demo123`

#### 4.4.2 预设分类

通过 API 创建预设分类：

```bash
# 创建所有预设分类
curl -X POST http://localhost:8080/api/v1/onboarding/create_defaults.json \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"type":"all"}'
```

---

## 5. 配置参考

### 5.1 application.yml 完整配置

```yaml
server:
  port: 8080

spring:
  application:
    name: bookkeeping
  
  datasource:
    url: jdbc:postgresql://localhost:5432/bookkeeping_dev
    username: bookkeeping
    password: test123
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 10
      minimum-idle: 5
      idle-timeout: 300000
      connection-timeout: 20000
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
  
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true

jwt:
  secret: ${JWT_SECRET:your-256-bit-secret-key-here-change-in-production}
  expiration: 86400000  # 24 hours in milliseconds

springdoc:
  api-docs:
    path: /v3/api-docs
  swagger-ui:
    path: /doc.html

logging:
  level:
    com.bookkeeping: INFO
    org.springframework: WARN
    org.hibernate: WARN
```

### 5.2 环境变量

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `DB_HOST` | 数据库主机 | localhost |
| `DB_PORT` | 数据库端口 | 5432 |
| `DB_NAME` | 数据库名 | bookkeeping |
| `DB_USER` | 数据库用户 | bookkeeping |
| `DB_PASSWORD` | 数据库密码 | test123 |
| `JWT_SECRET` | JWT 密钥 | (必须设置) |
| `SPRING_PROFILES_ACTIVE` | 激活的配置 | dev |

### 5.3 多数据库配置

开发/测试环境使用 3 个独立的 PostgreSQL 数据库：

```sql
-- scripts/init-databases.sql
CREATE USER bookkeeping WITH PASSWORD 'test123';
CREATE DATABASE bookkeeping_dev OWNER bookkeeping;
CREATE DATABASE bookkeeping_test OWNER bookkeeping;
CREATE DATABASE bookkeeping OWNER bookkeeping;
GRANT ALL PRIVILEGES ON DATABASE bookkeeping_dev TO bookkeeping;
GRANT ALL PRIVILEGES ON bookkeeping_test TO bookkeeping;
GRANT ALL PRIVILEGES ON bookkeeping TO bookkeeping;
```

---

## 6. 监控与日志

### 6.1 健康检查

```bash
# 检查应用健康状态
curl http://localhost:8080/actuator/health

# 响应示例
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "PostgreSQL",
        "validationQuery": "isValid()"
      }
    }
  }
}
```

### 6.2 日志配置

#### 6.2.1 日志级别

```yaml
logging:
  level:
    com.bookkeeping: DEBUG
    org.springframework.web: INFO
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

#### 6.2.2 日志文件输出

```yaml
logging:
  file:
    name: logs/bookkeeping.log
  logback:
    rollingpolicy:
      max-file-size: 10MB
      max-history: 30
```

### 6.3 Actuator 端点

| 端点 | 说明 |
|------|------|
| `/actuator/health` | 健康检查 |
| `/actuator/info` | 应用信息 |
| `/actuator/metrics` | 指标数据 |
| `/actuator/env` | 环境变量 |

### 6.4 API 监控

- **Swagger UI**: `http://localhost:8080/doc.html`
- **OpenAPI 文档**: `http://localhost:8080/v3/api-docs`

---

## 7. 安全设置

### 7.1 JWT 配置

```yaml
jwt:
  secret: ${JWT_SECRET}  # 必须使用强随机密钥
  expiration: 86400000   # 24 小时
```

**生成强密钥**:
```bash
openssl rand -base64 32
```

### 7.2 CORS 配置

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(
        "http://localhost:3000",
        "https://your-domain.com"
    ));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", configuration);
    return source;
}
```

### 7.3 公开接口白名单

无需认证即可访问的接口：

```
/api/v1/auth/**
/api/v1/accounts/templates.json
/health
/doc.html
/v3/api-docs
/swagger-ui/**
/actuator/health
```

### 7.4 密码安全

- 使用 BCrypt 加密存储
- 盐值长度: 10 字符
- 最小密码长度: 6 字符

---

## 8. 备份与恢复

### 8.1 数据库备份

```bash
# 备份
pg_dump -h localhost -U bookkeeping -d bookkeeping > backup_$(date +%Y%m%d).sql

# 压缩备份
pg_dump -h localhost -U bookkeeping -d bookkeeping | gzip > backup_$(date +%Y%m%d).sql.gz
```

### 8.2 恢复数据库

```bash
# 恢复
psql -h localhost -U bookkeeping -d bookkeeping < backup_20240527.sql

# 解压后恢复
gunzip -c backup_20240527.sql.gz | psql -h localhost -U bookkeeping -d bookkeeping
```

### 8.3 定时备份脚本

```bash
#!/bin/bash
# scripts/backup.sh

DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR=/opt/backups/bookkeeping
DB_NAME=bookkeeping
DB_USER=bookkeeping

# 创建备份目录
mkdir -p $BACKUP_DIR

# 执行备份
pg_dump -h localhost -U $DB_USER -d $DB_NAME | gzip > $BACKUP_DIR/backup_$DATE.sql.gz

# 删除 7 天前的备份
find $BACKUP_DIR -name "backup_*.sql.gz" -mtime +7 -delete

echo "Backup completed: backup_$DATE.sql.gz"
```

### 8.4 自动化备份 (cron)

```bash
# 编辑 crontab
crontab -e

# 每天凌晨 2 点执行备份
0 2 * * * /opt/bookkeeping/scripts/backup.sh >> /var/log/backup.log 2>&1
```

---

## 9. 故障排除

### 9.1 常见问题

#### 9.1.1 数据库连接失败

**症状**: `Connection refused` 或 `Connection timeout`

**排查步骤**:
1. 检查 PostgreSQL 是否运行: `pg_isready -h localhost -p 5432`
2. 检查端口是否开放: `netstat -tlnp | grep 5432`
3. 检查防火墙规则
4. 验证数据库凭据

#### 9.1.2 Flyway 迁移失败

**症状**: `FlywayException: Migration checksum mismatch`

**解决方案**:
```bash
# 方法 1: 标记迁移为已跳过
./gradlew flyway repair

# 方法 2: 清理并重新迁移（数据丢失！）
./gradlew flywayClean flywayMigrate
```

#### 9.1.3 JWT Token 过期

**症状**: `401 Unauthorized` 或 `Token expired`

**解决方案**:
- 使用 refresh token 获取新 access token
- 或重新登录

#### 9.1.4 端口占用

**症状**: `Port 8080 is already in use`

**解决方案**:
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8080
kill -9 <PID>
```

### 9.2 日志分析

```bash
# 查看错误日志
grep -i error logs/bookkeeping.log | tail -50

# 查看特定时间段的日志
grep "2026-05-27 14:" logs/bookkeeping.log

# 查看异常堆栈
grep -A 10 "Exception" logs/bookkeeping.log
```

### 9.3 性能问题

#### 9.3.1 慢查询

启用 SQL 日志:
```yaml
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

#### 9.3.2 连接池调优

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

---

## 10. 开发指南

### 10.1 代码结构

```
backend/src/main/java/com/bookkeeping/
├── common/                 # 公共组件
│   ├── BaseEntity.java    # 基类实体
│   ├── ApiResponse.java   # 统一响应
│   ├── ResultCode.java    # 错误码
│   ├── UnixTimeConverter.java
│   ├── Auditable.java
│   └── enums/
│       ├── AccountType.java
│       ├── CategoryType.java
│       └── TransactionType.java
├── config/                # 配置
│   ├── SecurityConfig.java
│   ├── OpenApiConfig.java
│   ├── CacheConfig.java
│   └── DataInitializer.java
├── core/                  # 核心业务
│   ├── account/
│   ├── category/
│   ├── transaction/
│   ├── tag/
│   ├── budget/
│   ├── exchange/
│   ├── insights/
│   ├── data/
│   └── dashboard/
├── exception/             # 异常
│   ├── BusinessException.java
│   └── GlobalExceptionHandler.java
├── infrastructure/         # 基础设施
│   ├── controller/
│   └── config/
└── supporting/            # 支持模块
    ├── auth/
    ├── user/
    ├── onboarding/
    └── security/
```

### 10.2 开发命令

```bash
cd backend

# 编译
./gradlew compileJava

# 运行测试
./gradlew test

# 集成测试（需要数据库）
./gradlew integrationTest

# 构建
./gradlew clean build

# 运行
./gradlew bootRun
```

### 10.3 添加新模块

1. **创建实体类**:
```java
@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MyEntity extends BaseEntity {
    private String name;
}
```

2. **创建 DTO**:
```java
@MapperAuto(sourceEntity = MyEntity.class, direction = Direction.From)
public record MyEntityDto(Long id, String name) {}
```

3. **创建 Repository**:
```java
@Repository
public interface MyEntityRepository extends JpaRepository<MyEntity, Long> {}
```

4. **创建 Service**:
```java
@Service
@RequiredArgsConstructor
@Transactional
public class MyEntityService {
    private final MyEntityRepository repository;
}
```

5. **创建 Controller**:
```java
@RestController
@RequestMapping("/api/v1/my-entities")
@RequiredArgsConstructor
public class MyEntityController {
    private final MyEntityService service;
}
```

6. **添加迁移脚本**:
```sql
-- V{N}__my_entity.sql
CREATE TABLE my_entities (...);
```

### 10.4 API 文档规范

所有 Controller 必须包含 OpenAPI 注解：

```java
@RestController
@RequestMapping("/api/v1/my-entities")
@RequiredArgsConstructor
@Tag(name = "My Entities", description = "My entity management")
public class MyEntityController {
    
    @Operation(summary = "Get all entities")
    @GetMapping
    public ApiResponse<List<MyEntityDto>> getAll() {
        return ApiResponse.success(service.getAll());
    }
}
```

### 10.5 测试规范

```bash
# 单元测试
./gradlew test

# 集成测试
./gradlew integrationTest

# 所有测试报告
./gradlew allTestsReport
```

---

## 附录 A: 系统端口

| 端口 | 服务 | 说明 |
|------|------|------|
| 5432 | PostgreSQL | 数据库 |
| 8080 | Backend | 后端 API |
| 3000 | Frontend (开发中) | 前端 |

## 附录 B: 错误码参考

| 类别 | 错误码范围 | 说明 |
|------|-----------|------|
| 1xxxx | 100001-199999 | 用户模块 |
| 2xxxx | 200001-299999 | 账户模块 |
| 3xxxx | 300001-399999 | 分类模块 |
| 4xxxx | 400001-499999 | 交易/标签模块 |
| 5xxxx | 500001-599999 | 预算模块 |
| 6xxxx | 600001-699999 | 汇率模块 |
| 9xxxx | 900001-999999 | 认证/通用模块 |

## 附录 C: 常用命令速查

```bash
# 启动数据库
./scripts/start-db.sh

# 运行应用
cd backend && ./gradlew bootRun

# 运行测试
cd backend && ./gradlew test

# 构建
cd backend && ./gradlew clean build

# 查看健康
curl http://localhost:8080/actuator/health

# 查看 API 文档
curl http://localhost:8080/v3/api-docs
```

---

**文档版本**: v1.0
**创建日期**: 2026-05-27
**最后更新**: 2026-05-27