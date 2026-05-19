# Bookkeeping System - MVP Implementation Plan

## MVP Scope

| Module | Priority | Why MVP |
|--------|----------|---------|
| User Authentication | P0 | Cannot use system without login |
| Account Management | P0 | Core entity for all transactions |
| Transaction Categories | P0 | Required for income/expense classification |
| Transaction Management | P0 | Core data - create, read, update, delete |
| Transaction Tags | P1 | Key filtering dimension, low dev cost |
| Home Dashboard | P1 | Landing page with summary statistics |
| Transaction Statistics | P1 | Basic pie/bar charts for analysis |
| Data Export (CSV) | P1 | Prevents data lock-in feeling |

**Out of scope for MVP:** Tag Groups, Templates, Scheduled Transactions, File Import (CSV/OFX/QIF/etc.), Exchange Rates, Insights Explorer, LLM/AI, MCP, 2FA, OAuth2, Application Lock, Reconciliation, Map, File Storage, Cloud Sync, Password Reset (forgot/reset via email).

---

## Architecture

```
bookkeeping/
├── backend/                    # Spring Boot 3 + Spring Data JPA
│   ├── src/main/java/com/bookkeeping/
│   │   ├── BookkeepingApplication.java
│   │   ├── config/            # Security, CORS, JPA config
│   │   ├── controller/        # REST API controllers
│   │   ├── service/           # Business logic interfaces
│   │   ├── service/impl/      # Service implementations
│   │   ├── repository/        # Spring Data JPA repositories
│   │   ├── entity/            # JPA entities (@Entity)
│   │   ├── dto/               # Request/Response DTOs (MapStruct mapping)
│   │   ├── common/            # Enums, constants, utils
│   │   ├── security/          # JWT provider, security filter
│   │   └── exception/         # Global exception handler
│   ├── src/main/resources/
│   │   ├── application.yml    # Main config
│   │   ├── application-dev.yml
│   │   └── db/migration/      # Flyway SQL migrations
│   └── pom.xml
│
├── frontend/                   # Nuxt 4 + Vue 3 + Vuetify 3
│   ├── pages/                  # File-based routing (app/router.options.ts)
│   ├── components/             # Reusable Vue components (auto-imported)
│   ├── composables/            # Shared composition logic (auto-imported)
│   ├── stores/                 # Pinia stores
│   ├── server/                 # Nuxt server routes (API proxy)
│   ├── middleware/              # Route middleware (auth guard)
│   ├── plugins/                # Vuetify, i18n plugins
│   ├── i18n/                   # Locale files (zh-CN + en)
│   ├── app.vue
│   ├── app/router.options.ts
│   ├── nuxt.config.ts
│   └── package.json
│
└── docs/                       # API docs reference
```

---

## Phase 1: Backend Foundation (Day 1-2)

### Task 1.1: Project Scaffolding
- Initialize Spring Boot 3 project with Maven
- Dependencies: spring-boot-starter-web, spring-boot-starter-security, spring-boot-starter-data-jpa, postgresql, flyway-core, jjwt, lombok, mapstruct, springdoc-openapi
- Configure `application.yml` (PostgreSQL datasource, JPA/Hibernate, server port 8080)

### Task 1.2: Database Schema
- Flyway migration scripts for MVP tables:
  - `users` (uid, username, email, nickname, password, salt, default_currency, language, first_day_of_week, created_at, updated_at)
  - `accounts` (account_id, uid, category, type, parent_account_id, name, display_order, icon, color, currency, balance, comment, hidden, created_at, updated_at)
  - `transaction_categories` (category_id, uid, type, parent_category_id, name, display_order, icon, color, hidden, created_at, updated_at)
  - `transactions` (transaction_id, uid, type, category_id, account_id, transaction_time, amount, related_id, related_account_id, related_account_amount, comment, hide_amount, created_at, updated_at)

### Task 1.3: Security Foundation
- JWT token generation/validation utility
- Spring Security filter chain (permit auth endpoints, protect /api/v1/**)
- UserDetailsService integration
- Password hashing (BCrypt + salt)

### Task 1.4: Common Infrastructure
- Unified response envelope (`{success, result, errorCode, errorMessage}`)
- Global exception handler with error codes
- Base entity with created_at/updated_at auto-fill
- CORS configuration for frontend dev server

---

## Phase 2: Auth & User APIs (Day 2-3)

### Task 2.1: User Entity & Repository
- `User` JPA entity with `@Entity`, `@Table`, Lombok `@Data`, `@Builder`
- Spring Data JPA `JpaRepository<User, Long>`
- User registration service (username/email uniqueness check)

### Task 2.2: Auth APIs
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/authorize.json` | POST | Login (username/email + password -> JWT) |
| `/api/register.json` | POST | Register new user with default categories |
| `/api/logout.json` | GET | Revoke current token |

### Task 2.3: User Profile APIs
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/users/profile/get.json` | GET | Get user profile |
| `/api/v1/users/profile/update.json` | POST | Update profile (nickname, currency, language, etc.) |

### Task 2.4: Token Management
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/tokens/refresh.json` | POST | Refresh JWT token |

---

## Phase 3: Account Management APIs (Day 3-4)

### Task 3.1: Account Entity & Repository
- `Account` JPA entity with 9 categories (Cash, Checking, Credit Card, etc.)
- Account extend JSONB field (last_reconciled_time, credit_card_statement_date)
- Spring Data JPA `JpaRepository<Account, Long>`

### Task 3.2: Account CRUD APIs
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/accounts/list.json` | GET | List accounts (with stats: net worth, total assets, total liabilities) |
| `/api/v1/accounts/get.json` | GET | Get single account |
| `/api/v1/accounts/add.json` | POST | Create account (with optional sub-accounts) |
| `/api/v1/accounts/modify.json` | POST | Modify account |
| `/api/v1/accounts/hide.json` | POST | Hide/unhide account |
| `/api/v1/accounts/delete.json` | POST | Delete account + sub-accounts |

---

## Phase 4: Category Management APIs (Day 4)

### Task 4.1: Category Entity & Repository
- `TransactionCategory` JPA entity with two-level hierarchy (parent_category_id)
- Spring Data JPA `JpaRepository<TransactionCategory, Long>`
- Seed 14 preset categories on user registration

### Task 4.2: Category CRUD APIs
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/transaction/categories/list.json` | GET | List categories (tree structure) |
| `/api/v1/transaction/categories/get.json` | GET | Get single category |
| `/api/v1/transaction/categories/add.json` | POST | Create category |
| `/api/v1/transaction/categories/modify.json` | POST | Modify category |
| `/api/v1/transaction/categories/hide.json` | POST | Hide/unhide |
| `/api/v1/transaction/categories/move.json` | POST | Reorder |
| `/api/v1/transaction/categories/delete.json` | POST | Delete |

---

## Phase 5: Transaction Management APIs (Day 5-7)

### Task 5.1: Transaction Entity & Repository
- `Transaction` JPA entity with `@SQLDelete` soft delete
- Transaction types: Modify Balance (1), Income (2), Expense (3), Transfer (4)
- DB types: MODIFY_BALANCE (1), INCOME (2), EXPENSE (3), TRANSFER_OUT (4), TRANSFER_IN (5)
- Transfer creates TWO DB records linked by related_id
- Spring Data JPA `JpaRepository<Transaction, Long>` + query methods

### Task 5.2: Transaction CRUD APIs
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/transactions/count.json` | GET | Count by filters |
| `/api/v1/transactions/list.json` | GET | Paginated list (cursor-based, max 50/page) |
| `/api/v1/transactions/list/by_month.json` | GET | All transactions for a month |
| `/api/v1/transactions/list/all.json` | GET | All transactions in time range |
| `/api/v1/transactions/get.json` | GET | Single transaction detail |
| `/api/v1/transactions/add.json` | POST | Create transaction (with UUID dedup) |
| `/api/v1/transactions/modify.json` | POST | Modify transaction |
| `/api/v1/transactions/delete.json` | POST | Delete single transaction |
| `/api/v1/transactions/batch_delete.json` | POST | Batch delete |

### Task 5.3: Transaction Statistics APIs
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/transactions/statistics.json` | GET | Categorical stats (income/expense by category) |
| `/api/v1/transactions/statistics/trends.json` | GET | Monthly trends (YYYYMM) |
| `/api/v1/transactions/amounts.json` | GET | Dashboard amount aggregations (today, week, month, year) |

---

## Phase 5.5: Transaction Tags APIs (Day 7)

### Task 5.5.1: Tag Entity & Repository
- `TransactionTag` JPA entity (tag_id, uid, name, display_order, hidden)
- `TransactionTagIndex` JPA entity for many-to-many (uid, tag_id, transaction_id)
- Spring Data JPA repositories with custom queries

### Task 5.5.2: Tag CRUD APIs
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/transaction/tags/list.json` | GET | List all tags |
| `/api/v1/transaction/tags/get.json` | GET | Get single tag |
| `/api/v1/transaction/tags/add.json` | POST | Create tag |
| `/api/v1/transaction/tags/add_batch.json` | POST | Batch create (multi-line input) |
| `/api/v1/transaction/tags/modify.json` | POST | Modify tag |
| `/api/v1/transaction/tags/hide.json` | POST | Hide/unhide |
| `/api/v1/transaction/tags/delete.json` | POST | Delete tag |

### Task 5.5.3: Tag Integration with Transactions
- Transaction create/modify: accept `tag_ids` array (max 10)
- Transaction list/get: include tags in response (`{tagId, name}`)
- Transaction filter: support basic tag filter via `tag_ids` query param
- Batch tag operations on transactions (add/remove/clear)

---

## Phase 5.6: Data Export API (Day 8)

### Task 5.6.1: CSV Export
| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/data/export.csv` | GET | Export filtered transactions as CSV download |

- Accepts same filter params as transaction list (type, categories, accounts, tags, keyword, time range)
- CSV headers: id, type, category, account, related_account, amount, time, comment, tags
- Response: `Content-Disposition: attachment; filename=transactions.csv`
- Stream write for large datasets (no in-memory accumulation)

---

## Phase 6: Frontend Foundation (Day 8-9)

### Task 6.1: Project Scaffolding
- Initialize Nuxt 4 project with `npx nuxi init`
- Dependencies: @nuxtjs/i18n, pinia, vuetify (nuxt-vuetify3 module), vue-echarts, @mdi/js
- Configure `nuxt.config.ts` (SSR mode, modules, runtime config for API base URL, proxy)
- Set up directory structure (pages, components, composables, stores, server, middleware, plugins, i18n)

### Task 6.2: Layout Shell
- `layouts/default.vue`: left nav drawer + top app bar + `<NuxtPage />` content area
- Navigation structure:
  - Overview (Home)
  - Transactions (list)
  - Accounts
  - Categories
  - Settings (profile)
- Responsive basics (Vuetify grid via nuxt-vuetify3 module)

### Task 6.3: Auth Store & Middleware
- `stores/auth.ts`: Pinia auth store (login, register, logout, token management)
- `composables/useApi.ts`: `useFetch`/`$fetch` wrapper with Bearer token injection + 401 handling
- `middleware/auth.ts`: redirect to `/login` if not authenticated
- Nuxt server routes (`server/api/`) for backend API proxy

---

## Phase 7: Frontend Pages (Day 9-14)

### Task 7.1: Login & Register Pages
- Login form (username/email + password)
- Register form (username, email, nickname, password, currency, language, first day of week)
- Error handling display

### Task 7.2: Home Dashboard
- Monthly expense card (total, income)
- Asset overview (total assets, total liabilities, net worth)
- Today/This week/This month/This year income & expense cards
- 12-month trend chart (bar chart)

### Task 7.3: Account Management Page
- Account list grouped by category (9 groups)
- Net worth / Total assets / Total liabilities header
- Create/Edit account dialog
- Account item: icon, name, color, balance, currency
- Context menu: edit, hide, delete

### Task 7.4: Category Management Page
- Three-column layout: Type tabs (Income/Expense/Transfer) | Main categories | Sub-categories
- Create/Edit category dialog (name, icon, color)
- Tree display with parent-child relationship

### Task 7.5: Transaction List Page
- Filter bar: type, date range (today/week/month/year/custom), keyword search, tag filter
- Transaction type tabs: All / Income / Expense / Transfer / Modify Balance
- Paginated transaction list (cursor-based, load more)
- Transaction card: type icon, amount, category name, account name, tags, time, comment
- Create transaction dialog (full form)
- Edit transaction dialog
- Delete confirmation
- Amount hide/show toggle

### Task 7.6: Transaction Create/Edit Dialog
- Transaction type toggle (Modify Balance / Income / Expense / Transfer)
- Amount input (with sign handling)
- Category selector (filtered by type, two-level)
- Account selector (source account)
- Target account selector (transfer only)
- Tag multi-select (max 10, chips)
- Date & time picker
- Comment textarea (255 max)
- Transaction time timezone offset (simplified for MVP)

### Task 7.7: Statistics Page
- Data type tabs: Expense Analysis / Income Analysis
- Chart type toggle: Pie chart / Bar chart
- Category breakdown visualization
- Time range selector
- Account/category filter (optional)

### Task 7.8: Tag Management Page
- Tag list with color dots and names
- Create tag dialog (name, color)
- Batch create (multi-line input)
- Edit / delete tag
- Hide/visible toggle

### Task 7.9: Data Export
- Export button on transaction list page
- Filters applied to export (same selection as current list view)
- Download CSV file

---

## Phase 8: Polish & Verification (Day 14-15)

### Task 8.1: Error Handling & Validation
- Frontend form validation (required fields, length limits)
- Backend request validation (JSR-303)
- Comprehensive error mapping (error codes -> user-friendly messages)
- Loading states on all pages

### Task 8.2: Testing
- Backend: Unit tests for service layer (JUnit 5 + Mockito)
- Backend: Controller integration tests (Spring MockMvc)
- Frontend: Smoke test all pages and flows

### Task 8.3: Internationalization
- i18n setup with zh-CN and en-US
- Extract all UI strings to locale files

---

## Database Tables (MVP)

```sql
-- Users
CREATE TABLE users (
    uid BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    username VARCHAR(32) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    nickname VARCHAR(64),
    password VARCHAR(64) NOT NULL,
    salt VARCHAR(10) NOT NULL,
    default_currency VARCHAR(3) DEFAULT 'CNY',
    language VARCHAR(10) DEFAULT 'zh-CN',
    first_day_of_week SMALLINT DEFAULT 0,
    default_account_id BIGINT,
    transaction_edit_scope SMALLINT DEFAULT 1,
    disabled BOOLEAN DEFAULT FALSE,
    deleted BOOLEAN DEFAULT FALSE,
    email_verified BOOLEAN DEFAULT FALSE,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT NOT NULL,
    deleted_unix_time BIGINT DEFAULT 0
);

-- Accounts
CREATE TABLE accounts (
    account_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    uid BIGINT NOT NULL REFERENCES users(uid),
    deleted BOOLEAN DEFAULT FALSE,
    category SMALLINT NOT NULL,  -- 1-9
    type SMALLINT DEFAULT 1,     -- 1=single, 2=multi-sub
    parent_account_id BIGINT DEFAULT 0,
    name VARCHAR(64) NOT NULL,
    display_order INT DEFAULT 0,
    icon BIGINT DEFAULT 0,
    color VARCHAR(6),
    currency VARCHAR(3) NOT NULL,
    balance BIGINT DEFAULT 0,
    comment VARCHAR(255),
    extend JSONB,
    hidden BOOLEAN DEFAULT FALSE,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT NOT NULL
);

-- Transaction Categories
CREATE TABLE transaction_categories (
    category_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    uid BIGINT NOT NULL REFERENCES users(uid),
    deleted BOOLEAN DEFAULT FALSE,
    type SMALLINT NOT NULL,       -- 1=income, 2=expense, 3=transfer
    parent_category_id BIGINT DEFAULT 0,
    name VARCHAR(64) NOT NULL,
    display_order INT DEFAULT 0,
    icon BIGINT DEFAULT 0,
    color VARCHAR(6),
    hidden BOOLEAN DEFAULT FALSE,
    comment VARCHAR(255),
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT NOT NULL
);

-- Transaction Tags
CREATE TABLE transaction_tags (
    tag_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    uid BIGINT NOT NULL REFERENCES users(uid),
    deleted BOOLEAN DEFAULT FALSE,
    name VARCHAR(64) NOT NULL,
    display_order INT DEFAULT 0,
    hidden BOOLEAN DEFAULT FALSE,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT NOT NULL
);

-- Transaction-Tag Association (M:N)
CREATE TABLE transaction_tag_index (
    uid BIGINT NOT NULL REFERENCES users(uid),
    tag_id BIGINT NOT NULL REFERENCES transaction_tags(tag_id),
    transaction_id BIGINT NOT NULL REFERENCES transactions(transaction_id) ON DELETE CASCADE,
    PRIMARY KEY (uid, tag_id, transaction_id)
);

-- Transactions
CREATE TABLE transactions (
    transaction_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    uid BIGINT NOT NULL REFERENCES users(uid),
    deleted BOOLEAN DEFAULT FALSE,
    type SMALLINT NOT NULL,       -- 1-modify_balance, 2-income, 3-expense, 4-transfer_out, 5-transfer_in
    category_id BIGINT DEFAULT 0,
    account_id BIGINT NOT NULL,
    transaction_time BIGINT NOT NULL,
    timezone_utc_offset SMALLINT DEFAULT 480,
    amount BIGINT NOT NULL,
    related_id BIGINT DEFAULT 0,
    related_account_id BIGINT DEFAULT 0,
    related_account_amount BIGINT DEFAULT 0,
    hide_amount BOOLEAN DEFAULT FALSE,
    comment VARCHAR(255),
    created_ip VARCHAR(39),
    scheduled_created BOOLEAN DEFAULT FALSE,
    created_unix_time BIGINT NOT NULL,
    updated_unix_time BIGINT NOT NULL,
    deleted_unix_time BIGINT DEFAULT 0
);

-- Token Records (for JWT session tracking)
CREATE TABLE token_records (
    token_id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    uid BIGINT NOT NULL REFERENCES users(uid),
    user_token VARCHAR(512) NOT NULL,
    token_type SMALLINT DEFAULT 1,
    user_agent VARCHAR(256),
    expired_unix_time BIGINT NOT NULL,
    last_seen BIGINT NOT NULL,
    created_unix_time BIGINT NOT NULL
);

CREATE INDEX idx_transactions_uid_time ON transactions(uid, transaction_time DESC);
CREATE INDEX idx_transactions_account ON transactions(account_id);
CREATE INDEX idx_accounts_uid ON accounts(uid);
CREATE INDEX idx_categories_uid_type ON transaction_categories(uid, type);
CREATE INDEX idx_tags_uid ON transaction_tags(uid);
CREATE INDEX idx_tag_index_transaction ON transaction_tag_index(transaction_id);
```

---

## Preset Categories (Seeded on Registration)

### Income (type=1)
| Name | Icon | Sub-categories |
|------|------|----------------|
| 职业收入 | work | 工资, 奖金, 兼职 |
| 投资收益 | trending_up | 股票, 基金, 利息 |
| 其他收入 | more_horiz | - |

### Expense (type=2)
| Name | Icon | Sub-categories |
|------|------|----------------|
| 餐饮 | restaurant | 三餐, 外卖, 零食 |
| 交通 | directions_car | 公交, 地铁, 打车, 加油 |
| 购物 | shopping_bag | 日用品, 服装, 数码 |
| 住房 | home | 房租, 水电, 物业 |
| 娱乐 | sports_esports | 游戏, 电影, 旅游 |
| 医疗 | local_hospital | 看病, 药品 |
| 教育 | school | 培训, 书籍 |
| 通讯 | smartphone | 话费, 网费 |

---

## Key Design Decisions

1. **Amount as BIGINT**: Store in smallest currency unit (cents/fen) to avoid floating point issues.
2. **Unix timestamps**: Follow original design, all times stored as BIGINT Unix seconds.
3. **Soft delete**: Use `@SQLDelete` + `@Where` annotations for JPA soft delete, not physical deletion.
4. **Transfer as two records**: One TRANSFER_OUT + one TRANSFER_IN linked by `related_id`.
5. **Cursor pagination**: Use `transaction_time` for forward/backward pagination instead of offset.
6. **Standard response envelope**: `{success: bool, result: T, errorCode?: int, errorMessage?: string}`.
7. **Error codes**: Follow `category * 100000 + subCategory * 1000 + index` convention.
8. **Entity / DTO mapping**: Use MapStruct interfaces for entity<->DTO conversion, keep service layer clean.
9. **Lombok**: All entities and DTOs use `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`.
10. **Nuxt auto-imports**: Leverage Nuxt's auto-import for components, composables, and `useFetch`/`$fetch`.
11. **Nuxt server routes**: `/server/api/` proxies backend requests, keeping backend URL server-side only.
12. **JPA specifications**: Complex dynamic queries (transaction filters) use `JpaSpecificationExecutor` + Specification.
