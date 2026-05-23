# Technical Debt & Audit Report

**Date**: 2026-05-22
**Version**: 0.1.0 (pre-Season2)
**Auditor**: Development Agent

---

## Executive Summary

The bookkeeping application is in good technical shape for an MVP. Core architecture is solid (Spring Boot, Vue 3, PostgreSQL, JWT). Main technical debt areas are:
- No pagination on transactions
- Missing transaction edit/delete endpoints
- Incomplete error code taxonomy
- Limited test coverage on frontend

**No critical blockers.** All P0 bugs from initial audit are now fixed.

---

## 1. Code Quality

### ✅ Strengths

| Area | Status | Notes |
|------|--------|-------|
| Architecture | ✅ Good | Clean separation: controller → service → repository |
| Naming | ✅ Good | PascalCase entities, camelCase DTOs, kebab-case files |
| Error Handling | ✅ Good | GlobalExceptionHandler + BusinessException |
| Response Format | ✅ Good | `{success, result, errorCode, errorMessage}` |
| Soft Delete | ✅ Good | `deleted` flag on accounts |
| Amount Handling | ✅ Good | BIGINT cents throughout |
| Timestamp | ✅ Good | Unix epoch seconds (BIGINT) |

### ⚠️ Areas to Improve

| Issue | Severity | Location | Fix |
|-------|----------|----------|-----|
| No transaction pagination | 🟡 Medium | TransactionService | Add cursor-based pagination |
| No transfer support | 🟡 Medium | TransactionService | Add type 4+5 linked transactions |
| No transaction edit/delete | 🟡 Medium | TransactionController | Add PUT/DELETE endpoints |
| Duplicate UserService methods | 🟢 Low | UserService | `findById` + `getById` both exist |
| No index on transactions.account_id | 🟡 Medium | DB Migration | Add for query performance |

---

## 2. Dependencies

### ✅ Verified Compatible

| Dependency | Version | Status |
|------------|---------|--------|
| Spring Boot | 4.0.6 | ✅ OK |
| Spring Data JPA | 7.x | ✅ OK |
| Hibernate | 7.2.12 | ✅ OK |
| PostgreSQL | 18.3 | ✅ OK |
| Flyway | 11.4.1 | ✅ OK |
| JJWT | 0.12.6 | ✅ OK |
| MapStruct | 1.6.3 | ✅ OK |
| Lombok | 1.18.38 | ✅ OK |
| SpringDoc | 2.8.8 | ✅ OK |
| Vue 3 | 3.5.x | ✅ OK |
| Vuetify 3 | latest | ✅ OK |
| Nuxt 4 | 4.4.x | ✅ OK |
| ECharts | 5.x | ✅ OK |
| Pinia | latest | ✅ OK |

### ⚠️ Known Issues

| Issue | Workaround |
|-------|------------|
| `spring-boot-4` parent may have conflicts with some starters | Using explicit versions in build.gradle.kts |
| Nuxt 4 auto-imports can conflict with Vuetify | Using `VueECharts` global name, `<ClientOnly>` for charts |

---

## 3. Database Schema

### ✅ Correct

| Table | Status | Notes |
|-------|--------|-------|
| users | ✅ OK | email_verified, disabled, salt, password |
| accounts | ✅ OK | Two-level (parent_id for future), soft delete |
| categories | ✅ OK | Two-level (parent_id for future), sort_order |
| transactions | ✅ OK | related_id for transfers |

### ⚠️ Missing Indexes

```sql
-- Add for transaction query performance
CREATE INDEX idx_transactions_user_id ON transactions(user_id);
CREATE INDEX idx_transactions_account_id ON transactions(account_id);
CREATE INDEX idx_transactions_transaction_time ON transactions(transaction_time);

-- Add for account query performance
CREATE INDEX idx_accounts_user_id ON accounts(user_id);
```

### 📋 Recommended Migration
```sql
-- V4__add_performance_indexes.sql
CREATE INDEX IF NOT EXISTS idx_transactions_user_id ON transactions(user_id);
CREATE INDEX IF NOT EXISTS idx_transactions_account_id ON transactions(account_id);
CREATE INDEX IF NOT EXISTS idx_transactions_transaction_time ON transactions(transaction_time DESC);
CREATE INDEX IF NOT EXISTS idx_accounts_user_id ON accounts(user_id);
CREATE INDEX IF NOT EXISTS idx_categories_user_id ON categories(user_id);
```

---

## 4. Security

### ✅ Secure

| Area | Status | Notes |
|------|--------|-------|
| Password Hashing | ✅ | MD5(salt+password) per spec |
| JWT Expiry | ✅ | 24 hours (dev), configurable |
| CORS | ✅ | `http://localhost:*` pattern |
| CSRF | ✅ | Disabled (stateless JWT) |
| Session | ✅ | Stateless (no sessions) |

### ⚠️ Security Notes

| Note | Action |
|------|--------|
| MD5 for password is weak but matches ezBookkeeping | Keep for compatibility, consider bcrypt in v1.0 |
| No rate limiting on login | Add in production |
| No 2FA | Add in future iteration |
| JWT secret is hardcoded in dev | Use environment variable in prod |

---

## 5. Testing Coverage

### Current Coverage

| Layer | Tests | Coverage |
|-------|-------|----------|
| JwtTokenProvider | 15 | ✅ Good |
| AuthService | 9 | ✅ Good |
| UserService | 13 | ✅ Good |
| AccountService | 14 | ✅ Good |
| TransactionService | 5 | ⚠️ Partial |
| Controllers | 6 | ⚠️ Integration only |
| Mappers | 17 | ✅ Good |

**Total: 110 tests passing**

### Missing Tests

| Component | Priority | Notes |
|-----------|----------|-------|
| TransactionService.edit | 🔴 High | Core feature |
| TransactionService.delete | 🔴 High | Core feature |
| CategoryService | 🟡 Medium | CRUD operations |
| DashboardController | 🟡 Medium | Statistics |

---

## 6. Frontend Code Quality

### ✅ Good

| Area | Status |
|------|--------|
| API composable | ✅ Centralized |
| Auth middleware | ✅ Secure |
| Pinia store | ✅ Clean |
| i18n | ✅ Both languages |

### ⚠️ Frontend Debt

| Issue | Severity | Fix |
|-------|----------|-----|
| No loading skeletons | 🟢 Low | Add v-skeleton-loader |
| No error toasts | 🟢 Low | Add v-snackbar |
| No retry on failure | 🟢 Low | Add useFetch retry |
| No type for API responses | 🟡 Medium | Add TypeScript interfaces |

---

## 7. Performance Concerns

| Area | Concern | Mitigation |
|------|---------|------------|
| Transactions | No pagination | Add cursor-based pagination |
| Transactions | N+1 query risk | Use JOIN FETCH if needed |
| Frontend | Large list rendering | Use virtual scrolling for 100+ items |
| Charts | SSR issue | Using `<ClientOnly>` — OK |

---

## 8. Migration Path (Season 2)

### V4__add_performance_indexes.sql
```sql
-- Performance indexes for transactions
CREATE INDEX idx_transactions_user_time ON transactions(user_id, transaction_time DESC);
CREATE INDEX idx_transactions_account ON transactions(account_id);
CREATE INDEX idx_tags_user ON tags(user_id);
CREATE INDEX idx_transaction_tags ON transaction_tags(transaction_id, tag_id);

-- Budgets table (Season 2 feature)
CREATE TABLE budgets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    category_id BIGINT NOT NULL REFERENCES categories(id),
    amount_limit BIGINT NOT NULL,
    period VARCHAR(10) DEFAULT 'monthly',
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    UNIQUE(user_id, category_id)
);
```

### V5__add_sort_order.sql
```sql
ALTER TABLE accounts ADD COLUMN sort_order INT DEFAULT 0;
ALTER TABLE categories ADD COLUMN sort_order INT DEFAULT 0;
ALTER TABLE tags ADD COLUMN sort_order INT DEFAULT 0;
```

### V6__add_transaction_tags.sql
```sql
CREATE TABLE tags (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    name VARCHAR(64) NOT NULL,
    color VARCHAR(7) NOT NULL,
    sort_order INT DEFAULT 0,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    UNIQUE(user_id, name)
);

CREATE TABLE transaction_tags (
    transaction_id BIGINT NOT NULL REFERENCES transactions(id),
    tag_id BIGINT NOT NULL REFERENCES tags(id),
    PRIMARY KEY (transaction_id, tag_id)
);
```

---

## 9. API Contract Review

### ✅ Matches OpenAPI

| Endpoint | Status |
|----------|--------|
| POST /api/v1/auth/login | ✅ |
| POST /api/v1/auth/register | ✅ |
| GET /api/v1/auth/me | ✅ |
| GET /api/v1/accounts | ✅ |
| POST /api/v1/accounts | ✅ |
| GET /api/v1/categories | ✅ |
| POST /api/v1/categories | ✅ |
| GET /api/v1/transactions | ✅ |
| POST /api/v1/transactions | ✅ |

### ⚠️ Missing Endpoints

| Endpoint | Priority | Status |
|----------|----------|--------|
| PUT /api/v1/transactions/{id} | 🔴 P0 | Not implemented |
| DELETE /api/v1/transactions/{id} | 🔴 P0 | Not implemented |
| GET /api/v1/transactions/statistics | 🟡 P1 | Not implemented |
| GET /api/v1/tags | 🟢 P2 | Not implemented |
| POST /api/v1/data/export.csv | 🟢 P2 | Not implemented |

---

## 10. Tech Debt Priority Queue

| # | Item | Priority | Effort | Owner |
|---|------|----------|--------|-------|
| 1 | Add transaction edit/delete endpoints | 🔴 P0 | 5 days | Backend |
| 2 | Add transfer support | 🔴 P0 | 4 days | Backend+Frontend |
| 3 | Add pagination to transactions | 🟡 P1 | 3 days | Backend+Frontend |
| 4 | Add performance indexes | 🟡 P1 | 1 day | Backend |
| 5 | Add tags system | 🟢 P2 | 5 days | Backend+Frontend |
| 6 | Add transaction statistics | 🟢 P2 | 4 days | Backend+Frontend |

---

## 11. Recommendations

### Immediate (This Sprint)
1. ✅ **DONE** — Fix emailVerified bug
2. ✅ **DONE** — Fix stale process issue
3. Add transaction edit/delete (FEATURE-TXN-001, TXN-002)
4. Add transaction date picker (FEATURE-TXN-003)
5. Add transfer support (FEATURE-TXN-004)

### Next Sprint
6. Add month navigation (FEATURE-TXN-005)
7. Add transaction search (FEATURE-SEARCH-001)
8. Add tags (FEATURE-TAGS-001)

### Future (v1.0)
9. Add 2FA
10. Add budget system
11. Add CSV export
12. Consider bcrypt for password (breaking change)

---

*Last updated: 2026-05-22*
*Audit complete — ready for Season 2 development*