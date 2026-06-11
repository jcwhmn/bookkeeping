# Model Refactor Plan — Apply Canonical Pattern to All Entities

**Date**: 2026-06-10
**Status**: Planning
**Goal**: Apply the canonical pattern (proven by Category refactor) to all 12 entity models and eliminate the `toBuilder()` inherited-id bug across the codebase.

---

## Table of Contents

1. [Background & Motivation](#background--motivation)
2. [The Canonical Pattern](#the-canonical-pattern)
3. [Critical Bug: `toBuilder()` Loses Inherited id](#critical-bug-tobuilder-loses-inherited-id)
4. [Current State Analysis](#current-state-analysis)
5. [Refactor Order (Dependency-First)](#refactor-order-dependency-first)
6. [Per-Model Plan (12 models)](#per-model-plan-12-models)
7. [Frontend Updates](#frontend-updates)
8. [Testing Strategy](#testing-strategy)
9. [Pre-Flight Checklist](#pre-flight-checklist)
10. [Post-Refactor Verification](#post-refactor-verification)
11. [Risk Assessment](#risk-assessment)
12. [Timeline & Effort Estimate](#timeline--effort-estimate)

---

## Background & Motivation

The Category model was the first to be refactored using the canonical pattern (see `openspec/changes/archive/2026-06-08-category-api-restructure/`). The refactor exposed a **critical bug** that was causing:

- 50+ duplicate categories in the dev database (data pollution)
- "I modified Shopping, but it remains no change" user reports
- Hidden UPDATE/INSERT behavior across all entity updates

This plan applies the same canonical pattern to all 12 remaining entity models to:

1. **Fix the data pollution bug** — 30 `toBuilder()` bug sites across 9 services
2. **Standardize the codebase** — Consistent RESTful APIs, DTOs, tests
3. **Eliminate legacy patterns** — `.json` suffix endpoints, manual constructors, etc.
4. **Improve observability** — Per-user checks, clear error codes, audit trail
5. **Set up future refactors** — Establish a tested pattern for the rest of the team

---

## The Canonical Pattern

The pattern was documented in `.pi/skills/bookkeeping-refactor/SKILL.md` after the Category refactor. It consists of 9 steps:

### Step 1: Entity
```java
@Entity
@Table(name = "<plural>")
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Xxx extends BaseEntity {
    // Fields only — no setters
    @Builder.Default
    private Boolean hidden = false;
}
```

### Step 2: DTO with MapStructPlus
```java
@MapperAuto(sourceEntity = Xxx.class, direction = Direction.From)
public record XxxDto(Long id, String name, ...) {}
```

### Step 3: Service
```java
@Service
@RequiredArgsConstructor
public class XxxService {
    private final XxxRepository xxxRepository;
    private final XxxMapper xxxMapper;  // auto-generated
    private final SecurityUtils securityUtils;

    public XxxDto createXxx(XxxCreateRequest req) { /* ... */ }

    public XxxDto updateXxx(Long id, XxxUpdateRequest req) {
        // CRITICAL: use applyUpdate() to preserve inherited id
        Xxx existing = xxxRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new BusinessException(...));
        Xxx updated = existing.applyUpdate(e -> e.toBuilder()
                .name(req.name())
                .build());
        return xxxMapper.toDto(xxxRepository.save(updated));
    }
}
```

### Step 4: Controller (RESTful)
```java
@RestController
@RequestMapping("/api/v1/xxxs")
@RequiredArgsConstructor
public class XxxController {
    private final XxxService xxxService;

    @GetMapping                    // No .json suffix
    public ApiResponse<List<XxxDto>> list() { ... }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<XxxDto> create(@RequestBody XxxCreateRequest req) { ... }

    public record XxxCreateRequest(String name, ...) {}
}
```

### Step 5–9: Tests, Frontend, Verification
See `bookkeeping-refactor` skill for full details.

---

## Critical Bug: `toBuilder()` Loses Inherited id

### The Bug

Lombok's `@Builder(toBuilder = true)` generates a `toBuilder()` method that copies **only fields declared in the current class**. Fields inherited from a parent class (like `BaseEntity.id`) are **NOT** included.

```java
@Entity
@Builder(toBuilder = true)
public class Category extends BaseEntity {
    private String name;  // ✅ copied by toBuilder()
    // ... other Category fields
    // id, createdAt, updatedAt are in BaseEntity — NOT copied!
}
```

When you do `entity.toBuilder().name("new").build()`:
- The new entity has `id = null`
- Hibernate sees a transient entity (no id) → executes **INSERT** instead of UPDATE
- Result: **Duplicate rows** instead of in-place updates

### Real-World Evidence

User reported: "I modified Shopping, but it remains no change; there are two Transportation items."

Investigation revealed:
- 50 categories in dev DB for userId=1
- Original 12 had IDs 1-13 with sortOrder 0
- 25 new duplicates had IDs 119-143 with **same names** as originals
- Classic INSERT pattern: new IDs assigned by PostgreSQL sequence
- **Cause**: Each `reorder` call created 5 new entities instead of updating existing 5

### The Fix (in `BaseEntity`)

```java
public <T extends BaseEntity> T applyUpdate(Function<T, T> updater) {
    @SuppressWarnings("unchecked")
    T self = (T) this;
    T built = updater.apply(self);
    built.id = this.id;             // ← preserve inherited id
    built.createdAt = this.createdAt;  // ← preserve created timestamp
    return built;
}
```

### Usage Rule

**Always use `applyUpdate()` for entity updates in service layer:**

```java
// ✅ CORRECT — preserves id, performs UPDATE
Xxx updated = existing.applyUpdate(e -> e.toBuilder()
    .name(newName)
    .build());
xxxRepository.save(updated);

// ❌ WRONG — loses id, performs INSERT (creates duplicate!)
Xxx updated = existing.toBuilder()
    .name(newName)
    .build();
xxxRepository.save(updated);
```

### Verification

After any refactor, **always verify** with a live test that the update path does NOT create duplicates:

```bash
# Count before
curl -s -X GET "/api/v1/xxxs" -H "Authorization: Bearer $TOKEN" | grep -oP '"id":' | wc -l
# Should be N

# Trigger update
curl -s -X PUT "/api/v1/xxxs/..." -d "..."

# Count after
curl -s -X GET "/api/v1/xxxs" -H "Authorization: Bearer $TOKEN" | grep -oP '"id":' | wc -l
# Should still be N (not N+something)
```

---

## Current State Analysis

### Models at a Glance

| # | Model | DTO Mapper | toBuilder Sites | RESTful | Frontend | Tests (U/I) |
|---|-------|-----------|----------------|---------|----------|-------------|
| ✅ | **Category** | ✅ | 0 (fixed) | ✅ | ✅ | 1/1 |
| 1 | **Token** | ❓ | 0 | ❌ legacy | ❌ | 0/0 |
| 2 | **ExchangeRate** | ❓ | 1 | ❌ | ❌ | 0/0 |
| 3 | **InsightsExplorer** | ❓ | 3 | ❓ | ❌ | 0/0 |
| 4 | **TagGroup** | ❓ | 0 | ❓ | ❌ | 0/0 |
| 5 | **Tag** | ❓ | 6 | ❌ legacy (11 .json) | ❌ | 0/1 |
| 6 | **Account** | ❓ | 7 | ❌ legacy (9 .json) | ❌ | 1/1 |
| 7 | **Budget** | ❓ | 1 | ✅ partial | ❌ | 0/0 |
| 8 | **TransactionTemplate** | ❓ | 3 | ❓ | ❌ | 0/0 |
| 9 | **TransactionPicture** | ❓ | 0 | ❓ | ❌ | 0/0 |
| 10 | **ScheduledTransaction** | ❓ | 1 | ❓ | ❌ | 0/0 |
| 11 | **Transaction** | ❓ | 8 | ❌ legacy (8 .json) | ✅ partial | 0/0 |

**Total bug sites remaining**: 30 (across 9 services)

### RESTful Endpoint Coverage

| Model | Legacy `.json` | Pure RESTful | Status |
|-------|----------------|--------------|--------|
| Category | 0 | ✅ GET/POST/PUT/PATCH | Done |
| Account | 9 | ❌ mostly legacy | Needs refactor |
| Budget | 0 | ✅ partial | Minor refactor |
| Tag | 11 | ❌ mostly legacy | Needs refactor |
| Token | 6 | ❌ mostly legacy | Needs refactor |
| Transaction | 8 | ❌ mostly legacy | Needs refactor |

### Per-Model References (Dependency Graph)

```
Token ─────────────┐  (no internal deps)
ExchangeRate ──────┤
InsightsExplorer ──┤
TagGroup ──────────┤
                   │
User ──────────────┤
                   ↓
              Account (used by many)
                   ↓
              Tag (→ TagGroup)
                   ↓
        Category ✅ DONE
                   ↓
              Budget (→ Category)
                   ↓
       TransactionTemplate (→ Category, Account, Tag)
                   ↓
        TransactionPicture (→ Transaction)
                   ↓
       ScheduledTransaction (→ Category, Account, Tag)
                   ↓
              Transaction (→ Category, Account, Tag)
                   ↓
              Dashboard (uses everything above)
```

---

## Refactor Order (Dependency-First)

To minimize cascading changes, models are refactored in **5 batches** based on internal dependencies:

### Batch 1: Standalone Models (No Internal Deps)

| # | Model | Effort | toBuilder | Frontend Pages |
|---|-------|--------|-----------|----------------|
| 1 | **Token** | 🟢 S | 0 | None |
| 2 | **ExchangeRate** | 🟢 S | 1 | None |
| 3 | **InsightsExplorer** | 🟡 M | 3 | None |
| 4 | **TagGroup** | 🟢 S | 0 | None |

**Why first**: Zero internal dependencies, can be refactored in isolation. Establishes the pattern for subsequent batches.

### Batch 2: Tag (Depends on TagGroup)

| # | Model | Effort | toBuilder | Frontend Pages |
|---|-------|--------|-----------|----------------|
| 5 | **Tag** | 🔴 L | 6 | None |

**Why next**: Builds on Batch 1's TagGroup, heavily used by Transaction.

### Batch 3: Account (Heavily Used, No Internal Deps)

| # | Model | Effort | toBuilder | Frontend Pages |
|---|-------|--------|-----------|----------------|
| 6 | **Account** | 🔴 L | 7 | None |

**Why before Transaction**: Refactoring Account first makes Transaction refactor cleaner.

### Batch 4: Models depending on Category ✅/Account

| # | Model | Effort | toBuilder | Frontend Pages |
|---|-------|--------|-----------|----------------|
| 7 | **Budget** | 🟡 M | 1 | None |
| 8 | **TransactionTemplate** | 🔴 L | 3 | None |

**Why**: Both use Category (already refactored ✅), making them easier to tackle.

### Batch 5: Complex Transactional Models

| # | Model | Effort | toBuilder | Frontend Pages |
|---|-------|--------|-----------|----------------|
| 9 | **TransactionPicture** | 🟡 M | 0 | None |
| 10 | **ScheduledTransaction** | 🟡 M | 1 | None |
| 11 | **Transaction** | 🔴 XL | 8 | 1 (partial) |

**Why last**: Highest complexity, depends on most other models, has most `toBuilder()` sites.

### Special: Frontend Updates (After All Backend)

| # | Task | Effort | Notes |
|---|------|--------|-------|
| 12 | **Frontend pages** | 🔴 L | Update all `pages/*.vue` to use new RESTful APIs |

**Why after backend**: Frontend can only be updated after backend APIs are stable.

---

## Per-Model Plan (12 models)

### ✅ Model 0: Category (DONE)

**Status**: Complete (committed and pushed to GitHub)
- 28 unit tests + 17 integration tests passing
- All RESTful endpoints working
- `toBuilder()` bug fixed using `BaseEntity.applyUpdate()`
- Frontend `pages/categories.vue` updated
- OpenSpec change `category-api-restructure` archived

**Reference**: `openspec/changes/archive/2026-06-08-category-api-restructure/`

---

### Model 1: Token

**Entity**: `core/token/Token.java`
**Service**: `core/token/TokenService.java`
**Controller**: `core/token/TokenController.java`
**Tests**: None

**Current State**:
- 0 `toBuilder()` sites (low risk)
- 6 legacy `.json` endpoints
- Manual constructor (needs `@RequiredArgsConstructor`)
- No DTO mapper (needs to be added)
- No tests

**Refactor Steps**:
1. Add `@MapperAuto` to `TokenDto` (if exists) or create new
2. Refactor `TokenService` to use request DTOs + `applyUpdate()`
3. Rewrite `TokenController` with RESTful endpoints, `@RequiredArgsConstructor`
4. Move legacy endpoint DTOs to nested `record`s in controller
5. Add 5-7 unit tests (use real `TokenDtoMapperConverter`)
6. Add 6-8 integration tests (use Apache HttpClient5)

**Endpoints**:
- `GET /api/v1/tokens` — list
- `POST /api/v1/tokens` — create
- `DELETE /api/v1/tokens/{id}` — revoke
- `GET /api/v1/tokens/active` — get active sessions

**Effort**: 1-2 hours

---

### Model 2: ExchangeRate

**Entity**: `core/exchange/ExchangeRate.java`
**Service**: `core/exchange/ExchangeRateService.java`
**Controller**: `core/exchange/ExchangeRateController.java`
**Tests**: None

**Current State**:
- 1 `toBuilder()` site (high risk)
- Legacy endpoints
- Manual constructor
- No DTO mapper
- No tests

**Refactor Steps**:
1. Add `@MapperAuto` to `ExchangeRateDto`
2. Fix 1 `toBuilder()` site → `applyUpdate()`
3. Rewrite `ExchangeRateController` with RESTful endpoints
4. Add 5-7 unit tests + 4-6 integration tests

**Endpoints**:
- `GET /api/v1/exchange-rates` — list
- `GET /api/v1/exchange-rates?base=USD&quote=EUR` — lookup
- `POST /api/v1/exchange-rates` — add new rate
- `PUT /api/v1/exchange-rates/{id}` — update

**Effort**: 1-2 hours

---

### Model 3: InsightsExplorer

**Entity**: `core/insights/InsightsExplorer.java`
**Service**: `core/insights/InsightsExplorerService.java`
**Controller**: `core/insights/InsightsExplorerController.java`
**Tests**: None

**Current State**:
- 3 `toBuilder()` sites (high risk)
- Manual constructor
- No DTO mapper
- No tests
- Complex UI state (displayOrder, hidden, custom config)

**Refactor Steps**:
1. Add `@MapperAuto` to `InsightsExplorerDto`
2. Fix 3 `toBuilder()` sites → `applyUpdate()`
3. Rewrite `InsightsExplorerController` with RESTful endpoints
4. Add 6-8 unit tests + 5-7 integration tests
5. Frontend page update (if exists)

**Endpoints**:
- `GET /api/v1/insights/explorers` — list
- `POST /api/v1/insights/explorers` — create
- `PUT /api/v1/insights/explorers/{id}` — update
- `PATCH /api/v1/insights/explorers/{id}/hidden` — hide/unhide
- `PUT /api/v1/insights/explorers/reorder` — reorder

**Effort**: 2-3 hours

---

### Model 4: TagGroup

**Entity**: `core/tag/TagGroup.java`
**Service**: `core/tag/TagService.java` (combined with Tag)
**Tests**: None (combined with Tag)

**Current State**:
- 0 `toBuilder()` sites (low risk)
- No DTO mapper
- No separate controller (combined with Tag)

**Refactor Steps**:
1. Add `@MapperAuto` to `TagGroupDto`
2. Refactor `TagService.createTagGroup()` and `updateTagGroup()` to use `applyUpdate()` (if any toBuilder)
3. Split `TagController` to have separate `/tag-groups` endpoints
4. Add 3-4 unit tests + 3-4 integration tests

**Endpoints** (new):
- `GET /api/v1/tag-groups` — list
- `POST /api/v1/tag-groups` — create
- `PUT /api/v1/tag-groups/{id}` — update
- `DELETE /api/v1/tag-groups/{id}` — delete

**Effort**: 1-2 hours

---

### Model 5: Tag (depends on TagGroup)

**Entity**: `core/tag/Tag.java`
**Service**: `core/tag/TagService.java`
**Controller**: `core/tag/TagController.java`
**Tests**: 0 unit, 1 integration (`TagControllerIntegrationTest`)

**Current State**:
- 6 `toBuilder()` sites (highest risk in Batch 2)
- 11 legacy `.json` endpoints
- Manual constructor
- Has DTO mapper but may need extension

**Refactor Steps**:
1. Add `@MapperAuto` to `TagDto` (verify coverage)
2. Fix 6 `toBuilder()` sites → `applyUpdate()`
3. Rewrite `TagController` (remove all `.json` endpoints, use RESTful)
4. Replace existing integration tests with new RESTful versions
5. Add 8-10 unit tests + 8-10 integration tests
6. Update `TagControllerIntegrationTest` to use RESTful paths

**Endpoints**:
- `GET /api/v1/tags` — list
- `GET /api/v1/tags?groupId=X` — filter by group
- `POST /api/v1/tags` — create
- `PUT /api/v1/tags/{id}` — update
- `PATCH /api/v1/tags/{id}/hidden` — hide/unhide
- `DELETE /api/v1/tags/{id}` — delete

**Effort**: 2-3 hours

---

### Model 6: Account

**Entity**: `core/account/Account.java`
**Service**: `core/account/AccountService.java`
**Controller**: `core/account/AccountController.java`
**Tests**: 1 unit, 1 integration

**Current State**:
- 7 `toBuilder()` sites (highest risk in Batch 3)
- 9 legacy `.json` endpoints
- Manual constructor
- Has `AccountMapper` but underutilized

**Refactor Steps**:
1. Verify `AccountDto` has `@MapperAuto` with all fields
2. Fix 7 `toBuilder()` sites → `applyUpdate()`
3. Rewrite `AccountController` (remove all `.json` endpoints)
4. Update existing unit test `AccountServiceTest` to use real mapper
5. Add 8-10 unit tests (new patterns)
6. Update `AccountControllerIntegrationTest` to use RESTful paths
7. Add 2-3 new integration tests (e.g., for transfer flow)

**Endpoints**:
- `GET /api/v1/accounts` — list
- `GET /api/v1/accounts/{id}` — get single
- `POST /api/v1/accounts` — create
- `PUT /api/v1/accounts/{id}` — update
- `DELETE /api/v1/accounts/{id}` — soft delete
- `PATCH /api/v1/accounts/{id}/hidden` — hide/unhide
- `GET /api/v1/accounts/{id}/balance` — current balance

**Effort**: 3-4 hours

---

### Model 7: Budget

**Entity**: `core/budget/Budget.java`
**Service**: `core/budget/BudgetService.java`
**Controller**: `core/budget/BudgetController.java`
**Tests**: 0 unit, 0 integration

**Current State**:
- 1 `toBuilder()` site
- Already mostly RESTful (per analysis)
- Manual constructor
- No DTO mapper (needs creation)

**Refactor Steps**:
1. Create `BudgetDto` with `@MapperAuto`
2. Fix 1 `toBuilder()` site → `applyUpdate()`
3. Add `@RequiredArgsConstructor` to `BudgetController`
4. Verify all endpoints are RESTful (no `.json`)
5. Add 5-7 unit tests + 5-7 integration tests

**Endpoints**:
- `GET /api/v1/budgets?year=2026&month=6` — list
- `POST /api/v1/budgets` — create
- `PUT /api/v1/budgets/{id}` — update
- `DELETE /api/v1/budgets/{id}` — delete

**Effort**: 2-3 hours

---

### Model 8: TransactionTemplate

**Entity**: `core/transaction/TransactionTemplate.java`
**Service**: `core/transaction/TransactionTemplateService.java`
**Controller**: `core/transaction/TransactionTemplateController.java`
**Tests**: 0 unit, 0 integration

**Current State**:
- 3 `toBuilder()` sites
- Manual constructor
- No DTO mapper
- No tests

**Refactor Steps**:
1. Add `@MapperAuto` to `TransactionTemplateDto`
2. Fix 3 `toBuilder()` sites → `applyUpdate()`
3. Rewrite `TransactionTemplateController` with RESTful endpoints
4. Add 5-7 unit tests + 5-7 integration tests
5. Frontend page update (if exists)

**Endpoints**:
- `GET /api/v1/transaction-templates` — list
- `POST /api/v1/transaction-templates` — create
- `PUT /api/v1/transaction-templates/{id}` — update
- `DELETE /api/v1/transaction-templates/{id}` — delete
- `POST /api/v1/transaction-templates/{id}/apply` — create transaction from template

**Effort**: 2-3 hours

---

### Model 9: TransactionPicture

**Entity**: `core/transaction/TransactionPicture.java`
**Service**: `core/transaction/TransactionPictureService.java`
**Controller**: `core/transaction/TransactionPictureController.java`
**Tests**: 0 unit, 0 integration

**Current State**:
- 0 `toBuilder()` sites (lowest risk in Batch 5)
- Manual constructor
- No DTO mapper

**Refactor Steps**:
1. Add `@MapperAuto` to `TransactionPictureDto`
2. Rewrite `TransactionPictureController` with RESTful endpoints
3. Add 3-5 unit tests + 3-5 integration tests
4. Frontend: receipt upload component update

**Endpoints**:
- `GET /api/v1/transactions/{id}/pictures` — list
- `POST /api/v1/transactions/{id}/pictures` — upload
- `DELETE /api/v1/transactions/{id}/pictures/{picId}` — delete

**Effort**: 2-3 hours

---

### Model 10: ScheduledTransaction

**Entity**: `core/transaction/ScheduledTransaction.java`
**Service**: `core/transaction/ScheduledTransactionService.java`
**Controller**: `core/transaction/ScheduledTransactionController.java`
**Tests**: 0 unit, 0 integration

**Current State**:
- 1 `toBuilder()` site
- Manual constructor
- No DTO mapper

**Refactor Steps**:
1. Add `@MapperAuto` to `ScheduledTransactionDto`
2. Fix 1 `toBuilder()` site → `applyUpdate()`
3. Rewrite `ScheduledTransactionController` with RESTful endpoints
4. Add 5-7 unit tests + 5-7 integration tests
5. Frontend page update (if exists)

**Endpoints**:
- `GET /api/v1/scheduled-transactions` — list
- `POST /api/v1/scheduled-transactions` — create
- `PUT /api/v1/scheduled-transactions/{id}` — update
- `DELETE /api/v1/scheduled-transactions/{id}` — delete
- `POST /api/v1/scheduled-transactions/{id}/execute` — manual trigger

**Effort**: 2-3 hours

---

### Model 11: Transaction (Most Complex)

**Entity**: `core/transaction/Transaction.java`
**Service**: `core/transaction/TransactionService.java`
**Controller**: `core/transaction/TransactionController.java`
**Tests**: 0 unit, 0 integration

**Current State**:
- 8 `toBuilder()` sites (highest of all models)
- 8 legacy `.json` endpoints
- Manual constructor
- Complex business logic (transfers create 2 linked records)
- Frontend `pages/transactions.vue` uses old API

**Refactor Steps**:
1. Verify `TransactionDto` has `@MapperAuto` with all fields
2. Fix 8 `toBuilder()` sites → `applyUpdate()`
3. Rewrite `TransactionController` (remove all `.json` endpoints, use RESTful)
4. Handle special cases (transfers, relatedId)
5. Add 12-15 unit tests (lots of business logic)
6. Add 12-15 integration tests
7. Update `pages/transactions.vue` to use new RESTful API

**Endpoints**:
- `GET /api/v1/transactions` — list with filters
- `GET /api/v1/transactions/{id}` — get single
- `POST /api/v1/transactions` — create (handles transfers automatically)
- `PUT /api/v1/transactions/{id}` — update
- `DELETE /api/v1/transactions/{id}` — soft delete
- `GET /api/v1/transactions/statistics?from=X&to=Y` — statistics

**Effort**: 6-8 hours (most complex)

---

## Frontend Updates

After all backend refactors are complete and APIs are stable, update the frontend pages to use the new RESTful APIs.

### Pages to Update

| Page | Current API | New API | Effort |
|------|-------------|---------|--------|
| `pages/categories.vue` | ✅ | ✅ | 0 (done) |
| `pages/accounts.vue` (if exists) | legacy | new | 2-3 hours |
| `pages/budgets.vue` (if exists) | legacy | new | 1-2 hours |
| `pages/tags.vue` (if exists) | legacy | new | 2-3 hours |
| `pages/transactions.vue` | legacy | new | 3-4 hours |
| `pages/scheduled.vue` (if exists) | legacy | new | 2-3 hours |

### Frontend Changes Per Page

1. Replace legacy API paths with RESTful ones
2. Remove `.json` suffix from paths
3. Use `api.get<T[]>(path)` directly (not wrapped `{ result }`)
4. Use `api.patch(path, body)` for hide/unhide
5. Use new envelope-unwrapping pattern (already done for categories)

---

## Testing Strategy

### Unit Tests (Service Layer)

**Pattern** (per `bookkeeping-refactor` skill):
```java
@ExtendWith(MockitoExtension.class)
class XxxServiceTest {
    @Mock private XxxRepository xxxRepository;
    @Mock private SecurityUtils securityUtils;
    private final XxxMapper xxxMapper = new XxxDtoMapperConverter();  // REAL
    
    @Nested
    @DisplayName("updateXxx")
    class UpdateXxx {
        @Test
        @DisplayName("preserves id (uses applyUpdate)")
        void preserves_id_using_applyUpdate() {
            // Setup
            when(xxxRepository.findByIdAndUserId(...)).thenReturn(Optional.of(existing));
            when(xxxRepository.save(any(Xxx.class))).thenAnswer(inv -> inv.getArgument(0));
            
            // Execute
            XxxDto result = xxxService.updateXxx(...);
            
            // Verify
            ArgumentCaptor<Xxx> captor = ArgumentCaptor.forClass(Xxx.class);
            verify(xxxRepository).save(captor.capture());
            assertEquals(existing.getId(), captor.getValue().getId());  // ← KEY ASSERTION
        }
    }
}
```

**Total unit tests**: ~80-100 across all models

### Integration Tests (Controller Layer)

**Pattern**:
```java
class XxxControllerIntegrationTest extends BaseIntegrationTest {
    // RestTemplate with Apache HttpClient5 (for PATCH support)
    
    @Test
    void updateXxx_withValidData_returnsUpdated() {
        // 1. Create test data
        // 2. Call PUT /api/v1/xxxs/{id}
        // 3. Assert response + verify in DB
    }
    
    @Test
    void reorderXxxs_doesNotCreateDuplicates() {
        // 1. Count before
        // 2. Reorder
        // 3. Count after — MUST be equal
    }
}
```

**Total integration tests**: ~60-80 across all models

### Live API Tests

For each model, run curl/PowerShell tests:
1. Create 5 entities
2. Update all 5 (verify count stays 5)
3. Reorder (verify count stays 5)
4. Delete (verify count drops)

---

## Pre-Flight Checklist

Before starting each refactor, ensure:

- [ ] Read `bookkeeping-refactor` skill
- [ ] Identify all `toBuilder()` sites in the service
- [ ] Check for `@Modifying` queries in repository (prefer get+save pattern)
- [ ] Note existing tests that may need updates
- [ ] Check if the entity has any `XxxRepository.@Modifying` queries
- [ ] Plan request DTOs as nested `record`s in controller
- [ ] Set up `applyUpdate()` replacement (it's in BaseEntity, no setup needed)

---

## Post-Refactor Verification

For each refactored model, verify:

### 1. Build
```bash
cd backend && ./gradlew compileJava
```

### 2. Unit Tests
```bash
cd backend && ./gradlew test --tests "com.bookkeeping.<package>.*Test"
```

### 3. Integration Tests
```bash
cd backend && ./gradlew integrationTest --tests "com.bookkeeping.<package>.*IntegrationTest"
```

### 4. Live Test (Critical for Bug Verification)

```bash
# Start backend
cd backend && ./gradlew bootRun --args='--spring.profiles.active=dev' &

# Login
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"demo","password":"demo123"}' \
  | grep -oP '"token":"\K[^"]+')

# Count before
BEFORE=$(curl -s -X GET "http://localhost:8080/api/v1/<model>" \
  -H "Authorization: Bearer $TOKEN" | grep -oP '"id":' | wc -l)
echo "Before: $BEFORE"

# Trigger update (e.g., reorder, hide, update)
curl -s -X PUT "http://localhost:8080/api/v1/<model>/..." \
  -H "Authorization: Bearer $TOKEN" -d "..."

# Count after
AFTER=$(curl -s -X GET "http://localhost:8080/api/v1/<model>" \
  -H "Authorization: Bearer $TOKEN" | grep -oP '"id":' | wc -l)
echo "After: $AFTER"

# MUST BE EQUAL
if [ "$BEFORE" != "$AFTER" ]; then
  echo "❌ FAIL: $BEFORE != $AFTER — bug still present!"
  exit 1
else
  echo "✅ PASS: no duplicates created"
fi
```

### 5. Frontend Test
- Open the relevant page in browser
- Verify API calls match new endpoints
- Test all CRUD operations
- Verify envelope unwrapping works

---

## Risk Assessment

### 🔴 High Risk

| Risk | Mitigation |
|------|-----------|
| Breaking existing functionality | Comprehensive integration tests + live API tests |
| Data corruption from bug fix | Run live count-before/after test on every refactor |
| Performance regression from `applyUpdate()` | Benchmark before/after (should be negligible) |
| Frontend-backend desync | Update frontend immediately after each backend refactor |

### 🟡 Medium Risk

| Risk | Mitigation |
|------|-----------|
| Test flakiness from shared state | Use unique test users per test (current pattern) |
| Transaction refactor breaks transfers | Extra thorough testing of transfer flow |
| API contract changes affect mobile/other clients | Document breaking changes in OpenSpec |

### 🟢 Low Risk

| Risk | Mitigation |
|------|-----------|
| Linter complaints | Follow existing patterns from Category refactor |
| Documentation drift | Update AGENTS.md, USER-GUIDE.md, ADMIN-GUIDE.md per refactor |

---

## Timeline & Effort Estimate

### Per-Batch Breakdown

| Batch | Models | Sites | Unit Tests | Intg Tests | Effort |
|-------|--------|-------|------------|------------|--------|
| 1 | Token, ExchangeRate, InsightsExplorer, TagGroup | 4 | 15-20 | 15-20 | 6-8 hours |
| 2 | Tag | 6 | 8-10 | 8-10 | 2-3 hours |
| 3 | Account | 7 | 8-10 | 8-10 | 3-4 hours |
| 4 | Budget, TransactionTemplate | 4 | 10-14 | 10-14 | 4-6 hours |
| 5 | TransactionPicture, ScheduledTransaction, Transaction | 9 | 20-25 | 20-25 | 10-14 hours |
| Frontend | All pages | - | - | - | 4-6 hours |
| **Total** | **12 models** | **30 sites** | **~60-80** | **~60-80** | **~30-40 hours** |

### Suggested Schedule

Assuming 4-6 hours of work per session:

| Session | Batch | Models | Tasks |
|---------|-------|--------|-------|
| 1 | Batch 1 | Token | Full refactor + tests |
| 2 | Batch 1 | ExchangeRate | Full refactor + tests |
| 3 | Batch 1 | InsightsExplorer | Full refactor + tests |
| 4 | Batch 1 | TagGroup | Full refactor + tests |
| 5 | Batch 2 | Tag | Full refactor + tests |
| 6 | Batch 3 | Account | Full refactor + tests |
| 7 | Batch 4 | Budget | Full refactor + tests |
| 8 | Batch 4 | TransactionTemplate | Full refactor + tests |
| 9 | Batch 5 | TransactionPicture | Full refactor + tests |
| 10 | Batch 5 | ScheduledTransaction | Full refactor + tests |
| 11-12 | Batch 5 | Transaction | Full refactor + tests (large) |
| 13 | Frontend | All pages | Update all pages |

**Total**: ~13 sessions × 4-6 hours = **~55-80 hours** for full execution.

---

## Success Criteria

The refactor is **complete** when:

- [ ] All 12 models use the canonical pattern
- [ ] All 30 `toBuilder()` sites replaced with `applyUpdate()`
- [ ] All legacy `.json` endpoints removed
- [ ] All manual constructors replaced with `@RequiredArgsConstructor`
- [ ] All unit tests pass (target: 200+)
- [ ] All integration tests pass (target: 80+)
- [ ] Live count-before/after tests pass for every model
- [ ] All frontend pages use new RESTful APIs
- [ ] All OpenSpec specs updated
- [ ] `AGENTS.md`, `USER-GUIDE.md`, `ADMIN-GUIDE.md` updated

---

## Related Documents

- `.pi/skills/bookkeeping-refactor/SKILL.md` — Canonical pattern reference
- `docs/refactoring/REFACTORING-ANALYSIS.md` — Older analysis (May 2026) — superseded
- `openspec/changes/archive/2026-06-08-category-api-restructure/` — Reference implementation
- `openspec/specs/category-*/spec.md` — Main specs for Category
- `AGENTS.md` — Project coding conventions

---

## Change Log

| Date | Change | Author |
|------|--------|--------|
| 2026-06-10 | Initial plan created | Claude + User |
| 2026-06-10 | Category refactor complete (Model 0) | Claude + User |
| 2026-06-10 | Plan based on learnings from Category refactor | Claude + User |

---

## Approval

This plan is ready for execution. Recommend starting with **Model 1: Token** as the first refactor task (smallest, easiest, validates the pattern end-to-end).
