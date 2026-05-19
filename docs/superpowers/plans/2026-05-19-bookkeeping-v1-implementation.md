# Bookkeeping Implementation Plan v1.0

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development or superpowers:executing-plans to implement this plan phase-by-phase. Each phase has a GATE that must pass verification before proceeding.

**Goal:** Build a working bookkeeping application with user authentication and account management

**Starting Version:** 0.0.1-SNAPSHOT

**Target Version:** 0.1.0 (M1 - User Auth + Accounts)

**Architecture:** Monolith (Spring Boot + Nuxt), PostgreSQL 18+, JWT Auth

---

## 🔑 Gate Definition

Each phase ends with a **GATE** - verification checkpoint:

| Gate | Check | Command |
|------|-------|---------|
| **G1** | Backend compiles | `cd backend && gradle compileJava` |
| **G2** | Backend runs | `curl http://localhost:8080/actuator/health` → `{"status":"UP"}` |
| **G3** | API responds | `curl http://localhost:8080/api/v1/auth/login` → valid JSON |
| **G4** | DB migrations pass | Check Flyway status in logs |
| **G5** | Unit tests pass | `cd backend && gradle test` |
| **G6** | Frontend compiles | `cd frontend && npm run build` |
| **G7** | Frontend runs | Browser → http://localhost:3000 |

**Gate Rules:**
- Must pass ALL checks in a gate before proceeding
- If gate fails, fix before continuing
- Document any issues in commit message

---

## Phase 1: Project Setup ⛩️

**Objective:** Create backend project structure with Gradle and database migrations

**Files to create:**
```
backend/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── src/main/
    ├── java/com/bookkeeping/BookkeepingApplication.java
    └── resources/
        ├── application.yml
        └── db/migration/
            ├── V1__init.sql
            └── V2__seed_data.sql
```

### Tasks:

- [ ] **Task 1.1:** Create `backend/build.gradle.kts`

```kotlin
plugins {
    java
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("io.freefair.lombok") version "9.0.0"
}

group = "com.bookkeeping"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    
    // Database
    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-database-postgresql")
    
    // API Documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.9.0")
    
    // Security
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
    
    // Caching
    implementation("com.github.ben-manes.caffeine:caffeine")
    
    // Utilities
    implementation("org.apache.commons:commons-lang3:3.15.0")
    implementation("commons-codec:commons-codec:1.16.1")
    
    // Lombok
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
    testCompileOnly("org.projectlombok:lombok:1.18.38")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.38")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
```

- [ ] **Task 1.2:** Create `backend/settings.gradle.kts`

```kotlin
rootProject.name = "bookkeeping"
```

- [ ] **Task 1.3:** Create `backend/gradle.properties`

```properties
org.gradle.jvmargs=-Xmx2g
org.gradle.parallel=true
org.gradle.caching=true
```

- [ ] **Task 1.4:** Create `backend/src/main/java/com/bookkeeping/BookkeepingApplication.java`

```java
package com.bookkeeping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BookkeepingApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookkeepingApplication.class, args);
    }
}
```

- [ ] **Task 1.5:** Create `backend/src/main/resources/application.yml`

```yaml
spring:
  application:
    name: bookkeeping
  datasource:
    url: jdbc:postgresql://localhost:5432/bookkeeping
    username: bookkeeping
    password: ${DB_PASSWORD:bookkeeping123}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true

server:
  port: 8080

management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: always

jwt:
  secret: ${JWT_SECRET:your-256-bit-secret-key-here-change-in-production}
  access-token-expiry: 1800
  refresh-token-expiry: 2592000

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
```

- [ ] **Task 1.6:** Create `backend/src/main/resources/db/migration/V1__init.sql`

```sql
-- Users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(32) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    nickname VARCHAR(64),
    password VARCHAR(64) NOT NULL,
    salt VARCHAR(10) NOT NULL,
    default_currency VARCHAR(3) DEFAULT 'USD',
    default_account_id BIGINT,
    language VARCHAR(10) DEFAULT 'en-US',
    email_verified BOOLEAN DEFAULT FALSE,
    disabled BOOLEAN DEFAULT FALSE,
    deleted BOOLEAN DEFAULT FALSE,
    deleted_unix_time BIGINT,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT NOT NULL,
    created_by BIGINT,
    modified_by BIGINT
);

-- Tokens table
CREATE TABLE tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    token VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL,
    user_agent TEXT,
    last_active_time BIGINT,
    expires_at BIGINT NOT NULL,
    created_unix_time BIGINT NOT NULL,
    created_by BIGINT,
    modified_by BIGINT,
    CONSTRAINT uk_tokens_token UNIQUE (token)
);

-- Accounts table
CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    balance BIGINT NOT NULL DEFAULT 0,
    icon VARCHAR(50),
    color VARCHAR(7),
    notes TEXT,
    include_in_total BOOLEAN DEFAULT TRUE,
    archived BOOLEAN DEFAULT FALSE,
    deleted BOOLEAN DEFAULT FALSE,
    deleted_unix_time BIGINT,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT NOT NULL,
    created_by BIGINT,
    modified_by BIGINT,
    CONSTRAINT uk_accounts_name_user UNIQUE (name, user_id)
);

-- Categories table
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    parent_id BIGINT REFERENCES categories(id),
    name VARCHAR(100) NOT NULL,
    type VARCHAR(10) NOT NULL,
    icon VARCHAR(50),
    color VARCHAR(7),
    sort_order INT DEFAULT 0,
    deleted BOOLEAN DEFAULT FALSE,
    deleted_unix_time BIGINT,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT NOT NULL,
    created_by BIGINT,
    modified_by BIGINT,
    CONSTRAINT uk_categories_name_user_type UNIQUE (name, user_id, type)
);

-- Tags table
CREATE TABLE tags (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    name VARCHAR(50) NOT NULL,
    color VARCHAR(7),
    icon VARCHAR(50),
    deleted BOOLEAN DEFAULT FALSE,
    deleted_unix_time BIGINT,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT NOT NULL,
    created_by BIGINT,
    modified_by BIGINT,
    CONSTRAINT uk_tags_name_user UNIQUE (name, user_id)
);

-- Transactions table
CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    type VARCHAR(20) NOT NULL,
    amount BIGINT NOT NULL,
    currency VARCHAR(3) NOT NULL,
    account_id BIGINT NOT NULL REFERENCES accounts(id),
    destination_account_id BIGINT REFERENCES accounts(id),
    category_id BIGINT REFERENCES categories(id),
    related_transaction_id BIGINT REFERENCES transactions(id),
    transaction_time BIGINT NOT NULL,
    notes TEXT,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT NOT NULL,
    created_by BIGINT,
    modified_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE,
    deleted_unix_time BIGINT
);

-- Transaction tags (N:M)
CREATE TABLE transaction_tags (
    transaction_id BIGINT NOT NULL REFERENCES transactions(id),
    tag_id BIGINT NOT NULL REFERENCES tags(id),
    PRIMARY KEY (transaction_id, tag_id)
);

-- Budgets table
CREATE TABLE budgets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    category_id BIGINT NOT NULL REFERENCES categories(id),
    amount BIGINT NOT NULL,
    period VARCHAR(20) NOT NULL,
    custom_period_start INT,
    rollover BOOLEAN DEFAULT FALSE,
    alert_threshold INT DEFAULT 80,
    enabled BOOLEAN DEFAULT TRUE,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT NOT NULL,
    created_by BIGINT,
    modified_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE,
    deleted_unix_time BIGINT
);

-- Templates table
CREATE TABLE templates (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL,
    amount BIGINT NOT NULL,
    currency VARCHAR(3) NOT NULL,
    account_id BIGINT REFERENCES accounts(id),
    category_id BIGINT REFERENCES categories(id),
    notes TEXT,
    schedule_type VARCHAR(20) NOT NULL,
    schedule_interval INT,
    schedule_day_of_week INT,
    schedule_day_of_month INT,
    schedule_month INT,
    next_run_time BIGINT,
    enabled BOOLEAN DEFAULT TRUE,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT NOT NULL,
    created_by BIGINT,
    modified_by BIGINT,
    deleted BOOLEAN DEFAULT FALSE,
    deleted_unix_time BIGINT
);

-- Template tags
CREATE TABLE template_tags (
    template_id BIGINT NOT NULL REFERENCES templates(id),
    tag_id BIGINT NOT NULL REFERENCES tags(id),
    PRIMARY KEY (template_id, tag_id)
);

-- Exchange rates
CREATE TABLE exchange_rates (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    from_currency VARCHAR(3) NOT NULL,
    to_currency VARCHAR(3) NOT NULL,
    rate DECIMAL(20, 10) NOT NULL,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT NOT NULL,
    CONSTRAINT uk_exchange_rates UNIQUE (user_id, from_currency, to_currency)
);

-- Indexes
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_tokens_user ON tokens(user_id);
CREATE INDEX idx_accounts_user ON accounts(user_id);
CREATE INDEX idx_categories_user ON categories(user_id);
CREATE INDEX idx_tags_user ON tags(user_id);
CREATE INDEX idx_transactions_user_time ON transactions(user_id, transaction_time DESC);
CREATE INDEX idx_transactions_account ON transactions(account_id);
CREATE INDEX idx_transactions_category ON transactions(category_id);
CREATE INDEX idx_budgets_user ON budgets(user_id);
CREATE INDEX idx_templates_user ON templates(user_id);
```

- [ ] **Task 1.7:** Create `backend/src/main/resources/db/migration/V2__seed_data.sql`

```sql
-- Insert default categories for demo user
INSERT INTO users (username, email, nickname, password, salt, default_currency, created_unix_time, updated_unix_time, created_by, modified_by)
VALUES ('demo', 'demo@example.com', 'Demo User', '5f4dcc3b5aa765d61d8327deb882cf99', 'salt1234', 'USD', 1717104000, 1717104000, 1, 1);

-- Income categories
INSERT INTO categories (user_id, name, type, icon, sort_order, created_unix_time, updated_unix_time, created_by, modified_by)
VALUES (1, 'Salary', 'INCOME', 'money', 1, 1717104000, 1717104000, 1, 1);
INSERT INTO categories (user_id, name, type, icon, sort_order, created_unix_time, updated_unix_time, created_by, modified_by)
VALUES (1, 'Bonus', 'INCOME', 'bonus', 2, 1717104000, 1717104000, 1, 1);
INSERT INTO categories (user_id, name, type, icon, sort_order, created_unix_time, updated_unix_time, created_by, modified_by)
VALUES (1, 'Investment', 'INCOME', 'trending_up', 3, 1717104000, 1717104000, 1, 1);
INSERT INTO categories (user_id, name, type, icon, sort_order, created_unix_time, updated_unix_time, created_by, modified_by)
VALUES (1, 'Gift', 'INCOME', 'card_giftcard', 4, 1717104000, 1717104000, 1, 1);

-- Expense categories
INSERT INTO categories (user_id, parent_id, name, type, icon, sort_order, created_unix_time, updated_unix_time, created_by, modified_by)
VALUES (1, NULL, 'Food', 'EXPENSE', 'restaurant', 10, 1717104000, 1717104000, 1, 1);
INSERT INTO categories (user_id, parent_id, name, type, icon, sort_order, created_unix_time, updated_unix_time, created_by, modified_by)
VALUES (1, 5, 'Groceries', 'EXPENSE', 'shopping_cart', 11, 1717104000, 1717104000, 1, 1);
INSERT INTO categories (user_id, parent_id, name, type, icon, sort_order, created_unix_time, updated_unix_time, created_by, modified_by)
VALUES (1, 5, 'Restaurants', 'EXPENSE', 'fastfood', 12, 1717104000, 1717104000, 1, 1);
INSERT INTO categories (user_id, name, type, icon, sort_order, created_unix_time, updated_unix_time, created_by, modified_by)
VALUES (1, 'Transport', 'EXPENSE', 'directions_car', 20, 1717104000, 1717104000, 1, 1);
INSERT INTO categories (user_id, name, type, icon, sort_order, created_unix_time, updated_unix_time, created_by, modified_by)
VALUES (1, 'Housing', 'EXPENSE', 'home', 30, 1717104000, 1717104000, 1, 1);
INSERT INTO categories (user_id, name, type, icon, sort_order, created_unix_time, updated_unix_time, created_by, modified_by)
VALUES (1, 'Utilities', 'EXPENSE', 'power', 40, 1717104000, 1717104000, 1, 1);
INSERT INTO categories (user_id, name, type, icon, sort_order, created_unix_time, updated_unix_time, created_by, modified_by)
VALUES (1, 'Healthcare', 'EXPENSE', 'local_hospital', 50, 1717104000, 1717104000, 1, 1);
INSERT INTO categories (user_id, name, type, icon, sort_order, created_unix_time, updated_unix_time, created_by, modified_by)
VALUES (1, 'Entertainment', 'EXPENSE', 'movie', 60, 1717104000, 1717104000, 1, 1);
INSERT INTO categories (user_id, name, type, icon, sort_order, created_unix_time, updated_unix_time, created_by, modified_by)
VALUES (1, 'Shopping', 'EXPENSE', 'shopping_bag', 70, 1717104000, 1717104000, 1, 1);
```

- [ ] **Task 1.8:** Generate Gradle wrapper

```bash
cd backend && gradle wrapper --gradle-version 9.3
```

### 🚪 GATE 1: Project Setup Verification

**Prerequisites:** PostgreSQL running on `localhost:5432`, database `bookkeeping` created

```bash
# G1.1: Compile
cd backend && gradle compileJava
# Expected: BUILD SUCCESSFUL

# G1.2: Run app and check health
cd backend && gradle bootRun &
sleep 10
curl http://localhost:8080/actuator/health
# Expected: {"status":"UP"}
curl http://localhost:8080/api-docs
# Expected: OpenAPI JSON

# G1.3: Stop app
pkill -f 'bootRun' || true
```

**Gate 1 Pass Criteria:**
- ✅ Compilation successful
- ✅ App starts and health endpoint returns UP
- ✅ Swagger UI accessible at /swagger-ui.html
- ✅ Flyway migrations executed (check logs for "Migration V1__init.sql succeeded")

---

## Phase 2: Common Infrastructure

**Objective:** Create base classes, common utilities, and configuration

**Files to create:**
```
backend/src/main/java/com/bookkeeping/
├── common/
│   ├── Auditable.java
│   ├── BaseEntity.java
│   ├── ApiResponse.java
│   ├── ResultCode.java
│   └── enums/
│       ├── AccountType.java
│       ├── TransactionType.java
│       ├── CategoryType.java
│       ├── TokenType.java
│       └── BudgetPeriod.java
├── exception/
│   ├── BusinessException.java
│   └── GlobalExceptionHandler.java
└── infrastructure/
    └── config/
        ├── SecurityConfig.java
        ├── OpenApiConfig.java
        └── CorsConfig.java
```

### Tasks:

- [ ] **Task 2.1:** Create `common/Auditable.java`
- [ ] **Task 2.2:** Create `common/BaseEntity.java`
- [ ] **Task 2.3:** Create `common/ApiResponse.java`
- [ ] **Task 2.4:** Create `common/ResultCode.java`
- [ ] **Task 2.5:** Create enums
- [ ] **Task 2.6:** Create `exception/BusinessException.java`
- [ ] **Task 2.7:** Create `exception/GlobalExceptionHandler.java`
- [ ] **Task 2.8:** Create config classes

### 🚪 GATE 2: Common Infrastructure Verification

```bash
# G2.1: Compile
cd backend && gradle compileJava
# Expected: BUILD SUCCESSFUL

# G2.2: Run and verify no errors
cd backend && gradle bootRun &
sleep 10
# Check logs for no errors
# Kill and check exit code
```

**Gate 2 Pass Criteria:**
- ✅ All base classes compile
- ✅ No startup errors
- ✅ Health endpoint still works

---

## Phase 3: User Module

**Objective:** User entity, repository, DTOs, service, and controller

**Files to create:**
```
backend/src/main/java/com/bookkeeping/supporting/user/
├── User.java
├── UserRepository.java
├── UserDto.java
├── UserService.java
└── UserController.java
```

### Tasks:

- [ ] **Task 3.1:** Create `User.java`
- [ ] **Task 3.2:** Create `UserRepository.java`
- [ ] **Task 3.3:** Create `UserDto.java` (Java Record)
- [ ] **Task 3.4:** Create `UserService.java`
- [ ] **Task 3.5:** Create `UserController.java`

### 🚪 GATE 3: User Module Verification

```bash
# G3.1: Compile
cd backend && gradle compileJava

# G3.2: Start app
cd backend && gradle bootRun &
sleep 15

# G3.3: Test user endpoint (returns empty for now)
curl http://localhost:8080/api/v1/users/me
# Expected: 401 Unauthorized (no auth)

# G3.4: Run tests
cd backend && gradle test
# Expected: All tests pass
```

**Gate 3 Pass Criteria:**
- ✅ User entity compiles
- ✅ User endpoint returns 401 (auth required, no 500)
- ✅ Unit tests pass

---

## Phase 4: Auth Module

**Objective:** JWT authentication with login/register endpoints

**Files to create:**
```
backend/src/main/java/com/bookkeeping/supporting/
├── security/
│   ├── JwtTokenProvider.java
│   └── JwtAuthenticationFilter.java
└── auth/
    ├── AuthController.java
    ├── AuthService.java
    ├── LoginRequest.java
    └── LoginResponse.java
```

### Tasks:

- [ ] **Task 4.1:** Create `JwtTokenProvider.java`
- [ ] **Task 4.2:** Create `JwtAuthenticationFilter.java`
- [ ] **Task 4.3:** Update `SecurityConfig.java` with JWT filter
- [ ] **Task 4.4:** Create `AuthController.java`
- [ ] **Task 4.5:** Create `AuthService.java`

### 🚪 GATE 4: Auth Module Verification

```bash
# G4.1: Compile
cd backend && gradle compileJava

# G4.2: Start app
cd backend && gradle bootRun &
sleep 15

# G4.3: Test login
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"password"}'
# Expected: {"success":true,"result":{"token":"...","user":{...}}}

# G4.4: Test with token
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"password"}' | jq -r '.result.token')
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/users/me
# Expected: User details
```

**Gate 4 Pass Criteria:**
- ✅ Login with demo user succeeds
- ✅ JWT token returned
- ✅ Authenticated endpoint works with token
- ✅ Invalid token returns 401

---

## Phase 5: Account Module

**Objective:** Account CRUD operations for core domain

**Files to create:**
```
backend/src/main/java/com/bookkeeping/core/account/
├── Account.java
├── AccountRepository.java
├── AccountDto.java
├── AccountService.java
└── AccountController.java
```

### Tasks:

- [ ] **Task 5.1:** Create `Account.java`
- [ ] **Task 5.2:** Create `AccountRepository.java`
- [ ] **Task 5.3:** Create `AccountDto.java` (Java Record)
- [ ] **Task 5.4:** Create `AccountService.java`
- [ ] **Task 5.5:** Create `AccountController.java`

### 🚪 GATE 5: Account Module Verification

```bash
# G5.1: Compile and test
cd backend && gradle compileJava && gradle test

# G5.2: Login and get token
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"password"}' | jq -r '.result.token')

# G5.3: Create account
curl -X POST http://localhost:8080/api/v1/accounts \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"name":"Cash","type":"CASH","currency":"USD","balanceStr":"1000"}'
# Expected: Account created

# G5.4: List accounts
curl http://localhost:8080/api/v1/accounts \
  -H "Authorization: Bearer $TOKEN"
# Expected: Array of accounts

# G5.5: Get account
curl http://localhost:8080/api/v1/accounts/1 \
  -H "Authorization: Bearer $TOKEN"
# Expected: Account details
```

**Gate 5 Pass Criteria:**
- ✅ Create account succeeds
- ✅ List accounts returns data
- ✅ Get account by ID works
- ✅ Update and delete work
- ✅ All tests pass

---

## Phase 6: Frontend Setup

**Objective:** Create Nuxt frontend with basic structure and login page

**Files to create:**
```
frontend/
├── package.json
├── nuxt.config.ts
├── tsconfig.json
├── app.vue
├── pages/
│   ├── index.vue
│   └── login.vue
├── layouts/
│   └── default.vue
├── stores/
│   └── auth.ts
├── composables/
│   └── useApi.ts
├── middleware/
│   └── auth.ts
└── i18n/
    ├── zh-CN.json
    └── en-US.json
```

### Tasks:

- [ ] **Task 6.1:** Create `package.json`
- [ ] **Task 6.2:** Create `nuxt.config.ts`
- [ ] **Task 6.3:** Create `app.vue`
- [ ] **Task 6.4:** Create pages and layouts
- [ ] **Task 6.5:** Create Pinia store
- [ ] **Task 6.6:** Create API composable

### 🚪 GATE 6: Frontend Verification

```bash
# G6.1: Install dependencies
cd frontend && npm install

# G6.2: Build
cd frontend && npm run build
# Expected: .output generated

# G6.3: Start dev server
cd frontend && npm run dev &
sleep 10

# G6.4: Check frontend
curl http://localhost:3000
# Expected: HTML page loads
```

**Gate 6 Pass Criteria:**
- ✅ `npm install` succeeds
- ✅ `npm run build` succeeds
- ✅ Dev server starts on port 3000
- ✅ Login page accessible

---

## Phase 7: Integration Test

**Objective:** End-to-end verification of the complete flow

### 🚪 GATE 7: Integration Verification

```bash
# G7.1: Backend running with all modules
cd backend && gradle bootRun &
sleep 15

# G7.2: Frontend running
cd frontend && npm run dev &
sleep 10

# G7.3: Login flow
# Open browser to http://localhost:3000/login
# Enter demo/demo123
# Should redirect to dashboard

# G7.4: API tests
# Test all CRUD operations via curl

# G7.5: Final test run
cd backend && gradle test
# Expected: All tests pass
```

**Gate 7 Pass Criteria:**
- ✅ Login flow works end-to-end
- ✅ Account creation works via UI
- ✅ All backend tests pass
- ✅ No console errors in browser

---

## Verification Summary

| Phase | Gate | Key Verification |
|-------|------|------------------|
| 1 | G1 | Backend compiles and starts |
| 2 | G2 | Base classes work, no errors |
| 3 | G3 | User endpoint returns 401 (auth) |
| 4 | G4 | Login returns JWT token |
| 5 | G5 | Account CRUD works |
| 6 | G6 | Frontend builds and runs |
| 7 | G7 | Full integration works |

---

## File Summary

```
bookkeeping/
├── backend/
│   ├── build.gradle.kts
│   ├── settings.gradle.kts
│   ├── gradle.properties
│   ├── gradlew
│   └── src/
│       ├── main/
│       │   ├── java/com/bookkeeping/
│       │   │   ├── BookkeepingApplication.java
│       │   │   ├── common/
│       │   │   ├── exception/
│       │   │   ├── supporting/
│       │   │   ├── core/
│       │   │   └── infrastructure/
│       │   └── resources/
│       │       ├── application.yml
│       │       └── db/migration/
│       └── test/
└── frontend/
    ├── package.json
    ├── nuxt.config.ts
    ├── pages/
    ├── stores/
    └── composables/
```

---

## Notes

- All entities extend `BaseEntity` for consistent fields
- DTOs are Java Records for immutability
- mapstruct-ext integration deferred (use manual mapping for now)
- Tests use H2 in-memory database
- Each gate must pass before proceeding to next phase