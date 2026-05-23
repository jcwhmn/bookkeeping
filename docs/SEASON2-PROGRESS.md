# Season 2 Progress

**Last Updated**: 2026-05-22

## ✅ Fully Implemented (12/12)

| Feature | Backend | Frontend | Status |
|---------|---------|----------|--------|
| Transaction Edit (PUT /{id}) | ✅ | ✅ | Complete |
| Transaction Delete (DELETE /{id}) | ✅ | ✅ | Complete |
| Transaction Date Picker | ✅ | ✅ | Complete |
| Transfer Support (auto-linked pair) | ✅ | ✅ | Complete |
| Month Navigation | ✅ | ✅ | Complete |
| Transaction Search/Filter | ✅ | ✅ | Complete |
| Tags Management (CRUD) | ✅ | ✅ | Complete |
| Statistics Dashboard | ✅ | ✅ | Complete |
| Enhanced Charts (Dashboard) | ✅ | ✅ | Complete |
| Budget Management | ✅ | ✅ | Complete |
| Reports Page | ✅ | ✅ | Complete |
| CSV Export | ✅ | ✅ | Complete |

## 🏗️ MapStruct+ Refactoring (v2.0.0) — Complete

### DTO-annotated pattern applied to all entities

5 DTOs now use `@MapperAuto` directly on the record:
- `UserDto` → `UserMapper` + `UserDtoMapperConverter`
- `AccountDto` → `AccountMapper` + `AccountDtoMapperConverter`
- `CategoryDto` → `CategoryMapper` + `CategoryDtoMapperConverter`
- `TransactionDto` → `TransactionMapper` + `TransactionDtoMapperConverter`
- `TagDto` → `TagMapper` + `TagDtoMapperConverter`

BudgetDto excluded — requires custom logic (spent calculation).

## Backend Files Created

```
backend/src/main/java/com/bookkeeping/core/budget/
├── Budget.java, BudgetDto.java
├── BudgetService.java, BudgetController.java
├── BudgetRepository.java
├── CreateBudgetRequest.java, UpdateBudgetRequest.java

backend/src/main/java/com/bookkeeping/core/tag/
├── Tag.java, TagDto.java, TagService.java, TagController.java
├── TagRepository.java, CreateTagRequest.java, UpdateTagRequest.java

backend/src/main/java/com/bookkeeping/core/transaction/
├── StatisticsDto.java, TransactionSearchParams.java
└── TransactionController.java (added /export endpoint)

backend/src/main/resources/db/migration/
├── V4__tags.sql
└── V5__budgets.sql
```

## Frontend Pages

```
frontend/pages/
├── index.vue        # Enhanced Dashboard
├── transactions.vue # CRUD + Month Nav + Search
├── accounts.vue     # Account management
├── categories.vue  # Category management
├── tags.vue        # Tags CRUD
├── statistics.vue  # Statistics charts
├── budgets.vue     # Budget management
└── reports.vue      # Reports + CSV Export
```

## Test Results

```
./gradlew test → BUILD SUCCESSFUL
```

## Running Services

| Service | URL | Status |
|---------|-----|--------|
| Backend | http://localhost:8080 | ✅ Running |
| Frontend | http://localhost:3000 | ✅ Running |

## API Endpoints Added

### Budgets
```
GET/POST   /api/v1/budgets?year=&month=
PUT/DELETE /api/v1/budgets/{id}
```

### CSV Export
```
GET /api/v1/transactions/export?year=&month=&accountId=
```

## Navigation Menu

| Page | Route |
|------|-------|
| Dashboard | `/` |
| Transactions | `/transactions` |
| Accounts | `/accounts` |
| Categories | `/categories` |
| Tags | `/tags` |
| Statistics | `/statistics` |
| Budgets | `/budgets` |
| Reports | `/reports` |

---

*12/12 features implemented — v0.2.0 complete* 🎉
*MapStruct+ DTO-annotated pattern applied to 5 DTOs*