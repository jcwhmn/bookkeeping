# Bookkeeping Architecture Design v1.0

## Project Overview

- **Project Name**: Bookkeeping
- **Version**: 1.0.0
- **Date**: 2026-05-19
- **Status**: Draft
- **Parent Document**: `2026-05-19-bookkeeping-v1-requirements.md`

---

## 1. Architecture Overview

### 1.1 System Type

**Monolithic Architecture** with clear domain separation

```
┌─────────────────────────────────────────────────────────────┐
│                      Frontend (Nuxt 4)                      │
│                   Vue 3 + Vuetify 3                         │
└─────────────────────────────┬───────────────────────────────┘
                              │ REST API
┌─────────────────────────────┴───────────────────────────────┐
│                      Backend (Spring Boot)                  │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Core      │  │  Supporting │  │    Infrastructure   │  │
│  │  Domain     │  │  Subdomain  │  │                     │  │
│  │             │  │             │  │  Security, Config   │  │
│  │  Account    │  │    User     │  │  Database, Cache   │  │
│  │  Transaction│  │  Auth       │  │  API Docs           │  │
│  │  Category   │  │  Token      │  │                     │  │
│  │  Tag        │  │             │  │                     │  │
│  │  Budget     │  │             │  │                     │  │
│  │  Report     │  │             │  │                     │  │
│  │  Template   │  │             │  │                     │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────┬───────────────────────────────┘
                              │
┌─────────────────────────────┴───────────────────────────────┐
│                      PostgreSQL 17+                        │
│                    Flyway Migration                         │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 Why Monolith?

| Factor | Decision | Reason |
|--------|----------|--------|
| Team Size | ~20 members | Small team, no need for microservices complexity |
| Scope | v1.0 is bounded | All features fit in one deployment unit |
| Complexity | Minimize ops | Single database, single deployment |
| Iteration Speed | Fast iteration | Easier to develop and test locally |
| Future Scale | Can split later | Domain separation allows future extraction |

**When to Consider Microservices:**
- Team grows to 50+ developers
- Need independent scaling of components
- Different deployment cadences for modules
- Performance isolation requirements

---

## 2. Project Structure

### 2.1 Directory Layout

```
bookkeeping/
├── backend/                          # Spring Boot application
│   ├── build.gradle.kts              # Gradle build (Kotlin DSL)
│   ├── settings.gradle.kts
│   ├── gradle.properties
│   └── src/main/
│       ├── java/com/bookkeeping/
│       │   ├── BookkeepingApplication.java
│       │   ├── config/               # Configuration classes
│       │   │   ├── SecurityConfig.java
│       │   │   ├── CorsConfig.java
│       │   │   └── OpenApiConfig.java
│       │   ├── common/               # Shared utilities
│       │   │   ├── ApiResponse.java
│       │   │   ├── ResultCode.java
│       │   │   └── enums/
│       │   ├── core/                 # Core Domain
│       │   │   ├── account/
│       │   │   │   ├── AccountController.java
│       │   │   │   ├── AccountService.java
│       │   │   │   ├── AccountRepository.java
│       │   │   │   └── dto/
│       │   │   ├── transaction/
│       │   │   ├── category/
│       │   │   ├── tag/
│       │   │   ├── budget/
│       │   │   ├── report/
│       │   │   └── template/
│       │   ├── supporting/           # Supporting Subdomain
│       │   │   ├── user/
│       │   │   │   ├── UserController.java
│       │   │   │   ├── UserService.java
│       │   │   │   └── dto/
│       │   │   ├── auth/
│       │   │   ├── token/
│       │   │   └── security/          # JWT, filters
│       │   ├── infrastructure/        # Cross-cutting
│       │   │   ├── exception/
│       │   │   └── mapper/            # MapStruct mappers
│       │   └── entity/               # JPA entities
│       └── resources/
│           ├── application.yml
│           ├── application-dev.yml
│           └── db/migration/         # Flyway migrations
│               ├── V1__init.sql
│               └── V2__test_data.sql
│
├── frontend/                         # Nuxt 4 application
│   ├── nuxt.config.ts
│   ├── package.json
│   ├── pages/                       # File-based routing
│   │   ├── index.vue                # Dashboard
│   │   ├── login.vue
│   │   ├── register.vue
│   │   ├── accounts/
│   │   │   └── index.vue
│   │   ├── transactions/
│   │   │   ├── index.vue
│   │   │   └── [id].vue
│   │   ├── categories/
│   │   ├── tags/
│   │   ├── budgets/
│   │   ├── reports/
│   │   └── settings/
│   ├── stores/                      # Pinia stores
│   │   ├── auth.ts
│   │   ├── accounts.ts
│   │   └── transactions.ts
│   ├── composables/                 # Reusable logic
│   │   ├── useApi.ts
│   │   ├── useCurrency.ts
│   │   └── useFormatter.ts
│   ├── layouts/
│   │   ├── default.vue
│   │   └── empty.vue
│   └── plugins/
│       └── vuetify.ts
│
├── docs/                            # Documentation
│   └── superpowers/
│       ├── specs/
│       └── plans/
│
└── .gitignore
```

### 2.2 Backend Package Structure

```
com.bookkeeping
├── config/           # Spring configurations
├── common/          # Shared DTOs, enums, utilities
├── entity/          # JPA entities (all domains)
├── dto/             # Request/Response DTOs
│   ├── request/     # Incoming DTOs
│   └── response/    # Outgoing DTOs
├── repository/      # Spring Data JPA repositories
├── service/         # Business logic interfaces + impls
├── controller/      # REST controllers
├── exception/       # Custom exceptions + handlers
├── mapper/          # MapStruct mapper interfaces
└── security/        # JWT, filters, config
```

### 2.3 Frontend Page Structure

```
pages/
├── index.vue                    # Dashboard (default layout)
├── login.vue                    # Login (empty layout)
├── register.vue                 # Registration (empty layout)
├── (authenticated)/             # Group with auth middleware
│   ├── accounts/
│   │   ├── index.vue           # Account list
│   │   └── [id].vue            # Account detail
│   ├── transactions/
│   │   ├── index.vue           # Transaction list + quick entry
│   │   ├── new.vue             # New transaction (detailed form)
│   │   └── [id].vue            # Transaction detail/edit
│   ├── categories/
│   │   └── index.vue           # Category management
│   ├── tags/
│   │   └── index.vue           # Tag management
│   ├── budgets/
│   │   ├── index.vue           # Budget list
│   │   └── [id].vue            # Budget detail/edit
│   ├── reports/
│   │   ├── index.vue           # Report dashboard
│   │   ├── monthly.vue          # Monthly summary
│   │   ├── category.vue         # Category breakdown
│   │   ├── cashflow.vue        # Cash flow analysis
│   │   └── export.vue          # Export functionality
│   └── settings/
│       ├── index.vue           # User settings
│       ├── profile.vue         # Profile settings
│       └── preferences.vue     # App preferences
```

---

## 3. Domain Design

### 3.1 Core Domain Entities

```
┌─────────────────────────────────────────────────────────────┐
│                      Core Domain                            │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────────┐         ┌──────────────┐                  │
│  │   Account    │───1:N───│ Transaction │                  │
│  │              │         │              │                  │
│  │ - name       │         │ - amount     │                  │
│  │ - type       │         │ - type       │                  │
│  │ - currency   │         │ - date       │                  │
│  │ - balance    │         │ - notes      │                  │
│  │ - ownerId    │         │              │                  │
│  │ - sharedWith │         │ ────N:1──────│                  │
│  └──────────────┘         │  Category    │                  │
│         │                 │              │                  │
│         │                 │ ────N:M──────│                  │
│         │                 │    Tag       │                  │
│         │                 │              │                  │
│         │                 │ ────N:1──────│                  │
│         │                 │  Template    │                  │
│         │                 └──────────────┘                  │
│         │                                                    │
│  ┌──────┴──────┐                                           │
│  │   Budget    │                                           │
│  │ - amount    │                                           │
│  │ - period    │                                           │
│  │ - alertAt   │                                           │
│  └─────────────┘                                            │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 3.2 Supporting Subdomain Entities

```
┌─────────────────────────────────────────────────────────────┐
│                   Supporting Subdomain                      │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  ┌──────────────┐         ┌──────────────┐                  │
│  │    User     │───1:1───│   Token      │                  │
│  │              │         │              │                  │
│  │ - username   │         │ - token      │                  │
│  │ - email      │         │ - type       │                  │
│  │ - password   │         │ - expiresAt  │                  │
│  │ - settings   │         │ - userId     │                  │
│  └──────────────┘         └──────────────┘                  │
│                                                             │
│  ┌──────────────┐                                           │
│  │  ExchangeRate│                                           │
│  │              │                                           │
│  │ - fromCurrency                                        │
│  │ - toCurrency                                           │
│  │ - rate      │                                           │
│  │ - userId    │                                           │
│  └─────────────┘                                           │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 3.3 Entity Relationships

| Entity A | Relationship | Entity B | Description |
|----------|--------------|----------|-------------|
| User | 1:N | Account | User owns accounts |
| User | 1:N | Transaction | User creates transactions |
| User | 1:N | Category | User owns categories |
| User | 1:N | Tag | User owns tags |
| User | 1:N | Budget | User owns budgets |
| User | 1:N | Template | User owns templates |
| User | 1:N | ExchangeRate | User sets rates |
| Account | 1:N | Transaction | Transactions affect account |
| Category | 1:N | Transaction | Transactions belong to category |
| Transaction | N:M | Tag | Transactions can have multiple tags |
| Budget | N:1 | Category | Budget is per category |
| Template | N:1 | Account | Template uses default account |
| Template | N:1 | Category | Template uses default category |

---

## 4. Database Design

### 4.1 Core Tables

```sql
-- Core Domain Tables

-- accounts: User's financial accounts
CREATE TABLE accounts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    name VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL, -- CASH, BANK, CREDIT_CARD, INVESTMENT, MOBILE_PAYMENT, LOYALTY, OTHER
    currency VARCHAR(3) NOT NULL,
    balance BIGINT NOT NULL DEFAULT 0, -- in cents/fen
    icon VARCHAR(50),
    color VARCHAR(7),
    notes TEXT,
    include_in_total BOOLEAN DEFAULT TRUE,
    archived BOOLEAN DEFAULT FALSE,
    deleted BOOLEAN DEFAULT FALSE,
    deleted_unix_time BIGINT,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT NOT NULL,
    CONSTRAINT uk_accounts_name_user UNIQUE (name, user_id)
);

-- account_sharing: Shared account access
CREATE TABLE account_sharing (
    id BIGSERIAL PRIMARY KEY,
    account_id BIGINT NOT NULL REFERENCES accounts(id),
    shared_with_user_id BIGINT NOT NULL REFERENCES users(id),
    permission VARCHAR(20) DEFAULT 'READ_WRITE', -- READ_ONLY, READ_WRITE
    created_unix_time BIGINT NOT NULL,
    CONSTRAINT uk_account_sharing UNIQUE (account_id, shared_with_user_id)
);

-- categories: Income/expense categories
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    parent_id BIGINT REFERENCES categories(id),
    name VARCHAR(100) NOT NULL,
    type VARCHAR(10) NOT NULL, -- INCOME, EXPENSE
    icon VARCHAR(50),
    color VARCHAR(7),
    sort_order INT DEFAULT 0,
    deleted BOOLEAN DEFAULT FALSE,
    deleted_unix_time BIGINT,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT NOT NULL,
    CONSTRAINT uk_categories_name_user_type UNIQUE (name, user_id, type)
);

-- tags: Transaction tags
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
    CONSTRAINT uk_tags_name_user UNIQUE (name, user_id)
);

-- transactions: Financial transactions
CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    type VARCHAR(20) NOT NULL, -- INCOME, EXPENSE, TRANSFER_OUT, TRANSFER_IN, MODIFY_BALANCE
    amount BIGINT NOT NULL, -- in cents/fen, always positive
    currency VARCHAR(3) NOT NULL,
    account_id BIGINT NOT NULL REFERENCES accounts(id),
    destination_account_id BIGINT REFERENCES accounts(id), -- for TRANSFER type
    category_id BIGINT REFERENCES categories(id),
    related_transaction_id BIGINT REFERENCES transactions(id), -- linked TRANSFER record
    transaction_time BIGINT NOT NULL, -- Unix timestamp
    notes TEXT,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT NOT NULL,
    deleted BOOLEAN DEFAULT FALSE,
    deleted_unix_time BIGINT,
    CONSTRAINT uk_transaction_time_id UNIQUE (transaction_time, id)
);

-- transaction_tags: Many-to-many relationship
CREATE TABLE transaction_tags (
    transaction_id BIGINT NOT NULL REFERENCES transactions(id),
    tag_id BIGINT NOT NULL REFERENCES tags(id),
    PRIMARY KEY (transaction_id, tag_id)
);

-- budgets: Category spending limits
CREATE TABLE budgets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    category_id BIGINT NOT NULL REFERENCES categories(id),
    amount BIGINT NOT NULL, -- in cents/fen
    period VARCHAR(20) NOT NULL, -- MONTHLY, WEEKLY, YEARLY, CUSTOM
    custom_period_start INT, -- Day of month (1-28) for CUSTOM period
    rollover BOOLEAN DEFAULT FALSE,
    alert_threshold INT DEFAULT 80, -- percentage
    enabled BOOLEAN DEFAULT TRUE,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT NOT NULL,
    deleted BOOLEAN DEFAULT FALSE,
    deleted_unix_time BIGINT,
    CONSTRAINT uk_budgets_category_period UNIQUE (category_id, period, custom_period_start)
);

-- templates: Recurring transaction templates
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
    schedule_type VARCHAR(20) NOT NULL, -- DAILY, WEEKLY, MONTHLY, YEARLY, INTERVAL
    schedule_interval INT, -- for INTERVAL type: every N days
    schedule_day_of_week INT, -- for WEEKLY: 0-6 (Sun-Sat)
    schedule_day_of_month INT, -- for MONTHLY/YEARLY: 1-28
    schedule_month INT, -- for YEARLY: 1-12
    next_run_time BIGINT, -- Unix timestamp
    enabled BOOLEAN DEFAULT TRUE,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT NOT NULL,
    deleted BOOLEAN DEFAULT FALSE,
    deleted_unix_time BIGINT
);

-- template_tags: Template default tags
CREATE TABLE template_tags (
    template_id BIGINT NOT NULL REFERENCES templates(id),
    tag_id BIGINT NOT NULL REFERENCES tags(id),
    PRIMARY KEY (template_id, tag_id)
);

-- exchange_rates: User-defined exchange rates
CREATE TABLE exchange_rates (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    from_currency VARCHAR(3) NOT NULL,
    to_currency VARCHAR(3) NOT NULL,
    rate DECIMAL(20, 10) NOT NULL, -- from/to ratio
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT NOT NULL,
    CONSTRAINT uk_exchange_rates UNIQUE (user_id, from_currency, to_currency)
);
```

### 4.2 Supporting Tables

```sql
-- Supporting Subdomain Tables

-- users: User accounts
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(32) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    nickname VARCHAR(64),
    password VARCHAR(64) NOT NULL, -- MD5(salt + password)
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
    last_login_unix_time BIGINT
);

-- tokens: Authentication tokens
CREATE TABLE tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    token VARCHAR(255) NOT NULL,
    type VARCHAR(20) NOT NULL, -- JWT, API, MCP
    user_agent TEXT,
    last_active_time BIGINT,
    expires_at BIGINT NOT NULL,
    created_unix_time BIGINT NOT NULL,
    CONSTRAINT uk_tokens_token UNIQUE (token)
);

-- password_reset_tokens: Password reset tokens
CREATE TABLE password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    token VARCHAR(255) NOT NULL,
    expires_at BIGINT NOT NULL,
    created_unix_time BIGINT NOT NULL,
    used BOOLEAN DEFAULT FALSE
);

-- email_verification_tokens: Email verification
CREATE TABLE email_verification_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    token VARCHAR(255) NOT NULL,
    expires_at BIGINT NOT NULL,
    created_unix_time BIGINT NOT NULL
);
```

### 4.3 Indexes

```sql
-- Performance indexes
CREATE INDEX idx_transactions_user_time ON transactions(user_id, transaction_time DESC);
CREATE INDEX idx_transactions_account ON transactions(account_id);
CREATE INDEX idx_transactions_category ON transactions(category_id);
CREATE INDEX idx_transactions_user_deleted ON transactions(user_id, deleted);
CREATE INDEX idx_accounts_user ON accounts(user_id);
CREATE INDEX idx_accounts_user_archived ON accounts(user_id, archived);
CREATE INDEX idx_categories_user ON categories(user_id);
CREATE INDEX idx_tags_user ON tags(user_id);
CREATE INDEX idx_budgets_user ON budgets(user_id);
CREATE INDEX idx_templates_user ON templates(user_id);
CREATE INDEX idx_tokens_user ON tokens(user_id);
CREATE INDEX idx_account_sharing_shared_with ON account_sharing(shared_with_user_id);
```

---

## 5. API Design

### 5.1 API Structure

```
Base URL: /api/v1

/api/v1
├── /auth
│   ├── POST   /register          # User registration
│   ├── POST   /login              # User login
│   ├── POST   /logout             # User logout
│   ├── POST   /refresh            # Refresh JWT token
│   └── POST   /password/reset    # Request password reset
│
├── /users
│   ├── GET    /me                 # Current user profile
│   ├── PUT    /me                 # Update current user
│   └── POST   /password          # Change password
│
├── /accounts
│   ├── GET    /                   # List user's accounts
│   ├── POST   /                   # Create account
│   ├── GET    /{id}               # Get account detail
│   ├── PUT    /{id}               # Update account
│   ├── DELETE /{id}               # Delete account
│   ├── GET    /{id}/transactions # Get account transactions
│   └── POST   /{id}/share         # Share account with user
│
├── /transactions
│   ├── GET    /                   # List transactions (paginated)
│   ├── POST   /                   # Create transaction
│   ├── GET    /{id}               # Get transaction detail
│   ├── PUT    /{id}               # Update transaction
│   ├── DELETE /{id}               # Delete transaction
│   └── POST   /bulk               # Bulk create transactions
│
├── /categories
│   ├── GET    /                   # List categories
│   ├── POST   /                   # Create category
│   ├── PUT    /{id}               # Update category
│   ├── DELETE /{id}               # Delete category
│   └── GET    /tree               # Get category tree
│
├── /tags
│   ├── GET    /                   # List tags
│   ├── POST   /                   # Create tag
│   ├── PUT    /{id}               # Update tag
│   └── DELETE /{id}               # Delete tag
│
├── /budgets
│   ├── GET    /                   # List budgets
│   ├── POST   /                   # Create budget
│   ├── GET    /{id}               # Get budget detail
│   ├── PUT    /{id}               # Update budget
│   ├── DELETE /{id}               # Delete budget
│   ├── GET    /status             # Get budget status for period
│   └── GET    /alert              # Get budget alerts
│
├── /templates
│   ├── GET    /                   # List templates
│   ├── POST   /                   # Create template
│   ├── GET    /{id}               # Get template detail
│   ├── PUT    /{id}               # Update template
│   ├── DELETE /{id}               # Delete template
│   └── POST   /{id}/run           # Run template manually
│
├── /reports
│   ├── GET    /monthly            # Monthly summary
│   ├── GET    /category           # Category breakdown
│   ├── GET    /cashflow            # Cash flow analysis
│   ├── GET    /balance            # Account balance summary
│   └── GET    /budget             # Budget report
│
├── /exchange-rates
│   ├── GET    /                   # List user's exchange rates
│   ├── POST   /                   # Create exchange rate
│   └── DELETE /{id}               # Delete exchange rate
│
└── /export
    ├── GET    /csv                # Export transactions as CSV
    └── GET    /pdf                # Export report as PDF
```

### 5.2 Request/Response Examples

**Create Transaction**
```http
POST /api/v1/transactions
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "type": "EXPENSE",
  "amount": 5000,           // 50.00 in cents
  "currency": "USD",
  "accountId": 123,
  "categoryId": 456,
  "transactionTime": 1717104000,  // Unix timestamp
  "notes": "Lunch with team",
  "tagIds": [789, 101]
}
```

**Response**
```json
{
  "success": true,
  "result": {
    "id": 1001,
    "type": "EXPENSE",
    "amount": 5000,
    "currency": "USD",
    "accountId": 123,
    "categoryId": 456,
    "transactionTime": 1717104000,
    "notes": "Lunch with team",
    "tags": [
      { "id": 789, "name": "business" },
      { "id": 101, "name": "team" }
    ],
    "createdUnixTime": 1717104100,
    "updatedUnixTime": 1717104100
  },
  "errorCode": null,
  "errorMessage": null
}
```

**Monthly Report**
```http
GET /api/v1/reports/monthly?year=2024&month=5&accountId=123
```

**Response**
```json
{
  "success": true,
  "result": {
    "year": 2024,
    "month": 5,
    "totalIncome": 5000000,   // 50000.00
    "totalExpense": 3000000,  // 30000.00
    "netSavings": 2000000,
    "topCategories": [
      { "categoryId": 1, "name": "Food", "amount": 800000 },
      { "categoryId": 2, "name": "Transport", "amount": 500000 }
    ],
    "comparison": {
      "previousMonth": {
        "totalIncome": 4500000,
        "totalExpense": 2800000,
        "netSavings": 1700000
      }
    }
  }
}
```

---

## 6. Tech Stack Details

### 6.1 Backend

| Component | Technology | Version |
|-----------|------------|---------|
| Framework | Spring Boot | 4.0.6 |
| Build Tool | Gradle (Kotlin DSL) | 9.3 |
| Java | OpenJDK | 25 |
| ORM | Spring Data JPA + Hibernate | 6.x |
| Database | PostgreSQL | 17+ |
| Migration | Flyway | 11.4.1 |
| Auth | JJWT | 0.12.6 |
| Mapping | MapStruct | 1.6.3 |
| Boilerplate | Lombok | 1.18.38 |
| API Docs | SpringDoc OpenAPI | 2.8.8 |
| Testing | JUnit 5 + Mockito | Latest |

### 6.2 Frontend

| Component | Technology | Version |
|-----------|------------|---------|
| Framework | Nuxt 4 | Latest |
| UI Library | Vue 3 (Composition API) | 3.x |
| Component Library | Vuetify 3 | 3.x |
| State Management | Pinia | Latest |
| HTTP Client | $fetch (Nuxt built-in) | - |
| Charts | ECharts + vue-echarts | 5.x |
| i18n | @nuxtjs/i18n | 9.x |
| Build Tool | Vite | Latest |

### 6.3 Development Tools

| Tool | Purpose |
|------|---------|
| Docker | Local development environment |
| pgAdmin / DBeaver | Database administration |
| IntelliJ IDEA | Backend development |
| VS Code | Frontend development |
| Postman / Insomnia | API testing |

---

## 7. Security Design

### 7.1 Authentication Flow

```
┌────────┐      ┌─────────┐      ┌──────────┐      ┌─────────┐
│ Client │      │ Backend │      │ Database │      │  Email  │
└────┬───┘      └────┬────┘      └────┬─────┘      └────┬────┘
     │               │               │                  │
     │ 1. Login      │               │                  │
     │──────────────>│               │                  │
     │               │ 2. Validate  │                  │
     │               │──────────────>│                  │
     │               │               │                  │
     │               │ 3. Check 2FA  │                  │
     │               │<──────────────│                  │
     │               │               │                  │
     │ 4. JWT Token   │ 5. Create    │                  │
     │<──────────────│──────────────>│                  │
     │               │               │                  │
```

### 7.2 Token Strategy

| Token Type | Storage | Lifetime | Refresh |
|-----------|---------|----------|---------|
| Access Token (JWT) | Memory/LocalStorage | 30 minutes | Required |
| Refresh Token | HttpOnly Cookie | 30 days | Manual |

### 7.3 Password Security

- Algorithm: MD5 with salt (following original design)
- Salt length: 10 characters
- Rate limiting: 5 failed attempts per 15 minutes

---

## 8. Error Handling

### 8.1 Error Code Format

`category * 100000 + subCategory * 1000 + index`

| Category | Range | Description |
|----------|-------|-------------|
| System | 1xxxxx | System errors |
| Auth | 201xxx | Authentication errors |
| User | 202xxx | User management errors |
| Account | 203xxx | Account errors |
| Transaction | 204xxx | Transaction errors |
| Category | 205xxx | Category errors |
| Tag | 206xxx | Tag errors |
| Budget | 207xxx | Budget errors |

### 8.2 Error Response Format

```json
{
  "success": false,
  "result": null,
  "errorCode": 204001,
  "errorMessage": "Transaction not found",
  "path": "/api/v1/transactions/999"
}
```

---

## 9. Deployment

### 9.1 Development

```bash
# Backend
cd backend
./gradlew bootRun  # http://localhost:8080

# Frontend
cd frontend
npm run dev        # http://localhost:3000
```

### 9.2 Production (Docker)

```yaml
# docker-compose.yml
services:
  app:
    image: bookkeeping-app:latest
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=prod
      - DB_HOST=postgres
    depends_on:
      - postgres

  postgres:
    image: postgres:17
    volumes:
      - postgres_data:/var/lib/postgresql/data
    environment:
      - POSTGRES_DB=bookkeeping
      - POSTGRES_USER=bookkeeping
      - POSTGRES_PASSWORD=${DB_PASSWORD}

volumes:
  postgres_data:
```

---

## 10. Open Questions (Architecture)

| # | Question | Options |
|---|----------|---------|
| AQ1 | Database pool size? | A: Default (10 connections) |
| AQ2 | JWT secret storage? | A: Environment variable |
| AQ3 | API rate limiting? | A: Per-endpoint limits |
| AQ4 | Frontend state persistence? | A: Pinia + localStorage |

---

## Appendix: Entity Field Summary

| Entity | Key Fields | Amount Storage |
|--------|-----------|----------------|
| Account | ownerId, sharedWith | balance (BIGINT) |
| Transaction | type, accountId, categoryId | amount (BIGINT) |
| Category | parentId, type | - |
| Tag | userId | - |
| Budget | categoryId, period, alertThreshold | amount (BIGINT) |
| Template | scheduleType, nextRunTime | amount (BIGINT) |
| ExchangeRate | fromCurrency, toCurrency | rate (DECIMAL) |