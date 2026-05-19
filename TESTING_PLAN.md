# Bookkeeping System — Validation & Testing Plan

## 1. Backend Unit Tests

### 1.1 Security Layer
| # | Test | What to Verify |
|---|------|---------------|
| U1 | JwtProvider.generateToken | Valid token contains correct uid, username, expiry |
| U2 | JwtProvider.validateToken | Invalid/expired/manipulated tokens rejected |
| U3 | JwtProvider.getUidFromToken | Correct uid extracted |
| U4 | SecurityUtils.getCurrentUid | Returns uid from authenticated context |
| U5 | Password encoding | MD5(salt+password) matches expected hash, salt uniqueness |

### 1.2 Service Layer
| # | Test | What to Verify |
|---|------|---------------|
| U6 | AuthService.register | New user created, duplicates rejected, preset categories seeded |
| U7 | AuthService.login | Valid credentials return token, invalid rejected, disabled user blocked |
| U8 | AuthService.changePassword | Old password verified, new password applies, old token revoked |
| U9 | AccountService.createAccount | Account created with correct fields, sub-accounts created |
| U10 | AccountService.modifyAccount | Fields updated, sub-accounts synced |
| U11 | AccountService.listAccounts | Grouped by category, net worth/assets/liabilities correct |
| U12 | AccountService.deleteAccount | Soft deleted, sub-accounts also deleted |
| U13 | CategoryService.createCategory | Parent/child relationship correct, display order assigned |
| U14 | CategoryService.seedPresetCategories | 31 categories created (income/expense/transfer presets) |
| U15 | CategoryService.deleteCategory | Sub-categories deleted first, soft delete applied |
| U16 | TransactionService.createTransaction | Income/Expense/Transfer/ModifyBalance all create correct DB records |
| U17 | TransactionService.transferLogic | Two records created (OUT+IN), relatedId linked, balances adjusted |
| U18 | TransactionService.balanceAdjustment | Account balances increase/decrease correctly per transaction type |
| U19 | TransactionService.modifyTransaction | Old balance reversed, new balance applied, tags updated |
| U20 | TransactionService.deleteTransaction | Balance reversed, related transfer record also deleted |
| U21 | TransactionService.listTransactions | Cursor pagination works, filters apply correctly |
| U22 | TransactionService.getStatistics | Correct category aggregation, percentage calculation |
| U23 | TransactionService.getTrends | Monthly income/expense/count correct |
| U24 | TransactionService.getAmounts | Dashboard aggregations (today/week/month/year) correct |
| U25 | Tag CRUD | Create, modify, delete, batch create, hide all work |
| U26 | Tag transaction limit | Max 10 tags per transaction enforced |

### 1.3 Validation & Edge Cases
| # | Test | What to Verify |
|---|------|---------------|
| U27 | Amount range | Amount ≤ -99,999,999,999 or ≥ 99,999,999,999 rejected |
| U28 | Comment length | Comment > 255 chars rejected |
| U29 | Transfer validation | Transfer without target account rejected |
| U30 | Duplicate username/email | Register with existing username/email rejected |
| U31 | Empty request fields | @Valid/@NotBlank fields rejected with proper error code |
| U32 | Transaction edit scope | User with edit_scope=0 cannot modify any transactions |
| U33 | Soft delete isolation | Deleted records excluded from all queries |

---

## 2. Backend Integration Tests (API Layer)

### 2.1 Auth Endpoints
| # | Test | Method | Endpoint | Verify |
|---|------|--------|----------|--------|
| I1 | Register success | POST | /api/register.json | 200, token returned, categories seeded |
| I2 | Register duplicate | POST | /api/register.json | 400, errorCode 201002 |
| I3 | Login success | POST | /api/authorize.json | 200, token + user info |
| I4 | Login bad credentials | POST | /api/authorize.json | 401, errorCode 201005 |
| I5 | Logout | GET | /api/logout.json | 200, token revoked |
| I6 | Token refresh | POST | /api/v1/tokens/refresh.json | 200, new token returned |
| I7 | Unauthenticated access | GET | /api/v1/accounts/list.json | 401, errorCode 100002 |

### 2.2 Account Endpoints
| # | Test | Method | Endpoint | Verify |
|---|------|--------|----------|--------|
| I8 | List accounts | GET | /api/v1/accounts/list.json | 200, grouped by category, stats present |
| I9 | Create account | POST | /api/v1/accounts/add.json | 200, all fields persisted |
| I10 | Get account | GET | /api/v1/accounts/get.json?id=1 | 200, correct account returned |
| I11 | Modify account | POST | /api/v1/accounts/modify.json | 200, fields updated |
| I12 | Hide account | POST | /api/v1/accounts/hide.json | 200, hidden flag toggled |
| I13 | Delete account | POST | /api/v1/accounts/delete.json | 200, soft deleted |
| I14 | Delete sub-account | POST | /api/v1/accounts/sub_account/delete.json | 200 |

### 2.3 Category Endpoints
| # | Test | Method | Endpoint | Verify |
|---|------|--------|----------|--------|
| I15 | List categories | GET | /api/v1/transaction/categories/list.json | 200, tree structure |
| I16 | Create category | POST | /api/v1/transaction/categories/add.json | 200 |
| I17 | Batch create | POST | /api/v1/transaction/categories/add_batch.json | 200 |
| I18 | Modify category | POST | /api/v1/transaction/categories/modify.json?id=1 | 200 |
| I19 | Delete category | POST | /api/v1/transaction/categories/delete.json | 200 |

### 2.4 Transaction Endpoints
| # | Test | Method | Endpoint | Verify |
|---|------|--------|----------|--------|
| I20 | Create income | POST | /api/v1/transactions/add.json | 200, type=2, balance increased |
| I21 | Create expense | POST | /api/v1/transactions/add.json | 200, type=3, balance decreased |
| I22 | Create transfer | POST | /api/v1/transactions/add.json | 200, type=4, two records, relatedId linked |
| I23 | Create modify balance | POST | /api/v1/transactions/add.json | 200, type=1 |
| I24 | List paginated | GET | /api/v1/transactions/list.json?count=10 | 200, cursor pagination |
| I25 | Get single | GET | /api/v1/transactions/get.json?id=1 | 200, includes tags |
| I26 | Modify transaction | POST | /api/v1/transactions/modify.json | 200, balance recalculated |
| I27 | Delete transaction | POST | /api/v1/transactions/delete.json | 200, balance reversed |
| I28 | Batch delete | POST | /api/v1/transactions/batch_delete.json | 200 |
| I29 | Statistics | GET | /api/v1/transactions/statistics.json | 200, category aggregation |
| I30 | Trends | GET | /api/v1/transactions/statistics/trends.json | 200, monthly breakdown |
| I31 | Amounts | GET | /api/v1/transactions/amounts.json | 200, time range aggregation |
| I32 | Count | GET | /api/v1/transactions/count.json | 200 |
| I33 | List by month | GET | /api/v1/transactions/list/by_month.json | 200 |

### 2.5 Tag & Export Endpoints
| # | Test | Method | Endpoint | Verify |
|---|------|--------|----------|--------|
| I34 | List tags | GET | /api/v1/transaction/tags/list.json | 200 |
| I35 | Create tag | POST | /api/v1/transaction/tags/add.json | 200 |
| I36 | Batch create tags | POST | /api/v1/transaction/tags/add_batch.json | 200 |
| I37 | Delete tag | POST | /api/v1/transaction/tags/delete.json | 200, tag_index cleaned |
| I38 | Export CSV | GET | /api/v1/data/export.csv | 200, valid CSV, Content-Disposition header |
| I39 | Transaction with tags | POST | /api/v1/transactions/add.json | 200, tags persisted in index table |
| I40 | Tag limit exceeded | POST | /api/v1/transactions/add.json | 400, 11 tags rejected |

### 2.6 Health & System
| # | Test | Method | Endpoint | Verify |
|---|------|--------|----------|--------|
| I41 | Health check | GET | /healthz.json | 200, no auth required |
| I42 | Version | GET | /api/systems/version.json | 200 |

---

## 3. Database Validation

| # | Check | Query / Method |
|---|-------|---------------|
| D1 | All 7 tables exist | `SELECT tablename FROM pg_catalog.pg_tables WHERE schemaname='public'` |
| D2 | Flyway history recorded | `SELECT * FROM flyway_schema_history` — V1 and V2 present |
| D3 | Test user exists | `SELECT * FROM users WHERE username='demo'` |
| D4 | Preset categories count | `SELECT COUNT(*) FROM transaction_categories WHERE uid=1` ≥ 31 |
| D5 | Test accounts present | `SELECT COUNT(*) FROM accounts WHERE uid=1` = 3 |
| D6 | Transfer related_id linked | `SELECT t1.transaction_id, t2.transaction_id FROM transactions t1 JOIN transactions t2 ON t1.related_id=t2.transaction_id` |
| D7 | Soft delete works | Create then delete a record; `deleted=true` but row still exists |
| D8 | Cascade delete | Delete a transaction; associated `transaction_tag_index` rows removed |
| D9 | Balance consistency | Sum of all transaction amounts per account matches account.balance |
| D10 | Unique constraints | `username` and `email` have unique indexes |

---

## 4. Frontend Smoke Tests (Manual)

| # | Flow | Steps |
|---|------|-------|
| F1 | Register new user | Fill form → submit → redirect to dashboard |
| F2 | Login | demo/demo123 → dashboard loads with stats |
| F3 | Logout | Click logout → redirect to login |
| F4 | Dashboard cards | Values non-zero, trend chart renders |
| F5 | Account list | 3 accounts shown, net worth calculated |
| F6 | Account CRUD | Create/edit/delete account flows work |
| F7 | Category tree | Income/Expense/Transfer tabs show categories with sub-items |
| F8 | Category CRUD | Create/edit/delete category works |
| F9 | Tag list | 3 tags visible, batch create works |
| F10 | Transaction list | 18 transactions visible, pagination works |
| F11 | Filter transactions | Type filter, keyword search filter correctly |
| F12 | Create income | Fill form → save → appears in list, balance changed |
| F13 | Create expense | Fill form → save → appears with negative amount |
| F14 | Create transfer | Select source+target → save → two records |
| F15 | Edit transaction | Click item → modify → save → updated |
| F16 | Delete transaction | Click delete → confirm → removed from list |
| F17 | Transaction tags | Create transaction with tags → tags show on list |
| F18 | Statistics page | Pie chart + bar chart render with data |
| F19 | CSV export | Click export → CSV file downloaded |
| F20 | Profile settings | Change nickname/language → saved |
| F21 | Password change | Old + new password → changed → relogin works |

---

## 5. Coverage Targets

| Layer | Target | Measurement |
|-------|--------|------------|
| Service layer | ≥ 80% line coverage | JaCoCo |
| Controller layer | ≥ 70% line coverage | MockMvc + JaCoCo |
| Repository layer | ≥ 90% method coverage | DataJpaTest |
| DTO validation | 100% of @Valid annotations tested | Controller tests |
| Exception handlers | 100% mapped to test cases | Controller tests |

---

## 6. Test Implementation Structure

```
backend/src/test/java/com/bookkeeping/
├── unit/
│   ├── security/
│   │   ├── JwtProviderTest.java
│   │   └── SecurityUtilsTest.java
│   └── service/
│       ├── AuthServiceTest.java
│       ├── AccountServiceTest.java
│       ├── CategoryServiceTest.java
│       ├── TransactionServiceTest.java
│       └── TagServiceTest.java
├── integration/
│   ├── AuthControllerTest.java
│   ├── AccountControllerTest.java
│   ├── CategoryControllerTest.java
│   ├── TransactionControllerTest.java
│   ├── TagControllerTest.java
│   └── DataExportControllerTest.java
└── config/
    └── TestConfig.java          # Shared test configuration
```

## 7. Test Dependencies (add to build.gradle.kts)

```kotlin
testImplementation("org.springframework.boot:spring-boot-starter-test")
testImplementation("org.springframework.security:spring-security-test")
testImplementation("com.h2database:h2")  // In-memory DB for tests
testRuntimeOnly("org.junit.platform:junit-platform-launcher")
```

## 8. Execution Order

1. Fix build.gradle.kts — add test dependencies
2. Write TestConfig.java — shared test fixtures
3. Unit tests: services first (core logic)
4. Unit tests: security layer
5. Integration tests: auth controller (prerequisite for all others)
6. Integration tests: account → category → tag → transaction controllers
7. Run full suite: `gradle test`
8. Generate coverage report: `gradle test jacocoTestReport`
9. Review failures, fix bugs
10. Re-run until all pass and coverage targets met
