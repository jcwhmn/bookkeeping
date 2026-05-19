# Bookkeeping Implementation Plan v1.0

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a working bookkeeping application with user authentication and account management

**Starting Version:** 0.0.1-SNAPSHOT

**Target Version:** 0.1.0 (M1 - User Auth + Accounts)

**Architecture:** Monolith (Spring Boot + Nuxt), PostgreSQL 18+, JWT Auth

---

## Phase 1: Project Setup

### 1.1 Backend Project Scaffold

**Files:**
- Create: `backend/build.gradle.kts`
- Create: `backend/settings.gradle.kts`
- Create: `backend/gradle.properties`
- Create: `backend/src/main/java/com/bookkeeping/BookkeepingApplication.java`

- [ ] **Step 1: Create build.gradle.kts**

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

- [ ] **Step 2: Create settings.gradle.kts**

```kotlin
rootProject.name = "bookkeeping"
```

- [ ] **Step 3: Create gradle.properties**

```properties
org.gradle.jvmargs=-Xmx2g
org.gradle.parallel=true
org.gradle.caching=true
```

- [ ] **Step 4: Create BookkeepingApplication.java**

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

- [ ] **Step 5: Create application.yml**

```yaml
spring:
  application:
    name: bookkeeping
  datasource:
    url: jdbc:postgresql://localhost:5432/bookkeeping
    username: bookkeeping
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
  flyway:
    enabled: true
    locations: classpath:db/migration

server:
  port: 8080

jwt:
  secret: ${JWT_SECRET}
  access-token-expiry: 1800  # 30 minutes
  refresh-token-expiry: 2592000  # 30 days

springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
```

- [ ] **Step 6: Create gradle wrapper**

Run: `cd backend && gradle wrapper --gradle-version 9.3`

---

### 1.2 Database Migration

**Files:**
- Create: `backend/src/main/resources/application.yml` (dev profile)
- Create: `backend/src/main/resources/db/migration/V1__init.sql`

- [ ] **Step 1: Create V1__init.sql**

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

- [ ] **Step 2: Create V2__seed_data.sql**

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

---

## Phase 2: Common Infrastructure

### 2.1 Base Entity and Interfaces

**Files:**
- Create: `backend/src/main/java/com/bookkeeping/common/Auditable.java`
- Create: `backend/src/main/java/com/bookkeeping/common/BaseEntity.java`
- Create: `backend/src/main/java/com/bookkeeping/common/ApiResponse.java`
- Create: `backend/src/main/java/com/bookkeeping/common/ResultCode.java`
- Create: `backend/src/main/java/com/bookkeeping/common/enums/`

- [ ] **Step 1: Create Auditable.java**

```java
package com.bookkeeping.common;

public interface Auditable {
    Long getCreatedBy();
    void setCreatedBy(Long createdBy);
    Long getModifiedBy();
    void setModifiedBy(Long modifiedBy);
}
```

- [ ] **Step 2: Create BaseEntity.java**

```java
package com.bookkeeping.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity implements Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    
    protected Boolean deleted = false;
    protected Long deletedUnixTime;
    protected Long createdUnixTime;
    protected Long updatedUnixTime;
    
    @Column(name = "created_by")
    protected Long createdBy;
    
    @Column(name = "modified_by")
    protected Long modifiedBy;
    
    @PrePersist
    protected void onCreate() {
        long now = System.currentTimeMillis() / 1000;
        this.createdUnixTime = now;
        this.updatedUnixTime = now;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedUnixTime = System.currentTimeMillis() / 1000;
    }
}
```

- [ ] **Step 3: Create ApiResponse.java**

```java
package com.bookkeeping.common;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    boolean success,
    T result,
    Integer errorCode,
    String errorMessage
) {
    public static <T> ApiResponse<T> success(T result) {
        return new ApiResponse<>(true, result, null, null);
    }
    
    public static <T> ApiResponse<T> error(Integer errorCode, String message) {
        return new ApiResponse<>(false, null, errorCode, message);
    }
}
```

- [ ] **Step 4: Create ResultCode.java**

```java
package com.bookkeeping.common;

public final class ResultCode {
    
    // System errors (1xxxxx)
    public static final int SYSTEM_ERROR = 100000;
    public static final int VALIDATION_ERROR = 100001;
    
    // Auth errors (201xxx)
    public static final int AUTH_INVALID_CREDENTIALS = 201001;
    public static final int AUTH_TOKEN_EXPIRED = 201002;
    public static final int AUTH_TOKEN_INVALID = 201003;
    
    // User errors (202xxx)
    public static final int USER_NOT_FOUND = 202001;
    public static final int USER_ALREADY_EXISTS = 202002;
    
    // Account errors (203xxx)
    public static final int ACCOUNT_NOT_FOUND = 203001;
    public static final int ACCOUNT_DUPLICATE_NAME = 203002;
    
    private ResultCode() {}
}
```

- [ ] **Step 5: Create enums**

Create enums for: `AccountType`, `TransactionType`, `CategoryType`, `TokenType`, `BudgetPeriod`

---

### 2.2 Configuration Classes

**Files:**
- Create: `backend/src/main/java/com/bookkeeping/infrastructure/config/SecurityConfig.java`
- Create: `backend/src/main/java/com/bookkeeping/infrastructure/config/OpenApiConfig.java`
- Create: `backend/src/main/java/com/bookkeeping/infrastructure/config/CorsConfig.java`

- [ ] **Step 1: Create SecurityConfig.java**

```java
package com.bookkeeping.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/login", "/api/v1/auth/register").permitAll()
                .requestMatchers("/swagger-ui/**", "/api-docs/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

## Phase 3: User Module (Supporting Subdomain)

### 3.1 User Entity and Repository

**Files:**
- Create: `backend/src/main/java/com/bookkeeping/supporting/user/User.java`
- Create: `backend/src/main/java/com/bookkeeping/supporting/user/UserRepository.java`
- Create: `backend/src/main/java/com/bookkeeping/supporting/user/UserDto.java`

- [ ] **Step 1: Create User.java**

```java
package com.bookkeeping.supporting.user;

import com.bookkeeping.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {
    
    @Column(nullable = false, unique = true, length = 32)
    private String username;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(length = 64)
    private String nickname;
    
    @Column(nullable = false, length = 64)
    private String password;
    
    @Column(nullable = false, length = 10)
    private String salt;
    
    @Column(length = 3)
    private String defaultCurrency = "USD";
    
    private Long defaultAccountId;
    
    @Column(length = 10)
    private String language = "en-US";
    
    @Column(name = "email_verified")
    private Boolean emailVerified = false;
    
    private Boolean disabled = false;
}
```

- [ ] **Step 2: Create UserRepository.java**

```java
package com.bookkeeping.supporting.user;

import com.bookkeeping.supporting.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}
```

### 3.2 User Service and Controller

**Files:**
- Create: `backend/src/main/java/com/bookkeeping/supporting/user/UserService.java`
- Create: `backend/src/main/java/com/bookkeeping/supporting/user/UserController.java`
- Create: `backend/src/main/java/com/bookkeeping/supporting/user/UserService.java`

---

## Phase 4: Account Module (Core Domain)

### 4.1 Account Entity and Repository

**Files:**
- Create: `backend/src/main/java/com/bookkeeping/core/account/Account.java`
- Create: `backend/src/main/java/com/bookkeeping/core/account/AccountRepository.java`
- Create: `backend/src/main/java/com/bookkeeping/core/account/AccountDto.java`

### 4.2 Account Service and Controller

**Files:**
- Create: `backend/src/main/java/com/bookkeeping/core/account/AccountService.java`
- Create: `backend/src/main/java/com/bookkeeping/core/account/AccountController.java`

---

## Phase 5: Auth Module (Supporting Subdomain)

### 5.1 JWT Security

**Files:**
- Create: `backend/src/main/java/com/bookkeeping/supporting/security/JwtTokenProvider.java`
- Create: `backend/src/main/java/com/bookkeeping/supporting/security/JwtAuthenticationFilter.java`
- Create: `backend/src/main/java/com/bookkeeping/supporting/auth/AuthController.java`
- Create: `backend/src/main/java/com/bookkeeping/supporting/auth/AuthService.java`

---

## Phase 6: Frontend Setup

**Files:**
- Create: `frontend/nuxt.config.ts`
- Create: `frontend/package.json`
- Create: `frontend/pages/index.vue`
- Create: `frontend/pages/login.vue`
- Create: `frontend/stores/auth.ts`
- Create: `frontend/composables/useApi.ts`

---

## Phase 7: Testing

### Backend Tests

- [ ] **Step 1: User entity test**
- [ ] **Step 2: Account repository test**
- [ ] **Step 3: Auth integration test**

---

## Verification Steps

After each phase:

```bash
# Backend
cd backend
gradle build -x test
gradle test

# Frontend  
cd frontend
npm install
npm run dev
```

---

## File Summary

```
backend/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── src/main/
    ├── java/com/bookkeeping/
    │   ├── BookkeepingApplication.java
    │   ├── common/
    │   │   ├── Auditable.java
    │   │   ├── BaseEntity.java
    │   │   ├── ApiResponse.java
    │   │   ├── ResultCode.java
    │   │   └── enums/
    │   ├── supporting/
    │   │   ├── user/
    │   │   ├── auth/
    │   │   └── security/
    │   ├── core/
    │   │   └── account/
    │   └── infrastructure/
    │       └── config/
    └── resources/
        ├── application.yml
        └── db/migration/
            ├── V1__init.sql
            └── V2__seed_data.sql
```

---

## Notes

- All entities extend `BaseEntity` for consistent fields
- DTOs are Java Records for immutability
- mapstruct-ext integration deferred (use manual mapping for now)
- Tests use H2 in-memory database