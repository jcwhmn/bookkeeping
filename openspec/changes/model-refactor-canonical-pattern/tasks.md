# Model Refactor — Canonical Pattern — Tasks

> **Status legend**: `[ ]` = not started, `[x]` = completed

---

## 1. Pre-Flight Setup

- [ ] 1.1 Snapshot dev database: `pg_dump bookkeeping_dev > pre-refactor-backup.sql`
- [ ] 1.2 Clean duplicate rows from dev DB (use `data clear` endpoint or manual SQL)
- [ ] 1.3 Verify baseline: all 115 unit tests + 17 integration tests pass
- [ ] 1.4 Verify `build.gradle.kts` has `httpclient5` for integrationTest (already added in Category refactor)
- [ ] 1.5 Create base git worktree pattern: `git worktree add ../bookkeeping-batch1 -b refactor/batch1-standalone`

---

## 2. Batch 1: Standalone Models (Token, ExchangeRate, InsightsExplorer, TagGroup)

### 2.1 Token

- [ ] 2.1.1 Verify/add `@MapperAuto` to `TokenDto`
- [ ] 2.1.2 Audit `TokenService` for `toBuilder()` (currently 0 sites — should be safe)
- [ ] 2.1.3 Refactor `TokenController` to RESTful endpoints, add `@RequiredArgsConstructor`
- [ ] 2.1.4 Add nested `record` request DTOs to controller
- [ ] 2.1.5 Write 5-7 unit tests in `TokenServiceTest` (use real `TokenDtoMapperConverter`)
- [ ] 2.1.6 Write 6-8 integration tests in `TokenControllerIntegrationTest` (PATCH support via Apache HttpClient5)
- [ ] 2.1.7 Run live API test: count-before/after to verify no duplicates
- [ ] 2.1.8 Commit and push branch

### 2.2 ExchangeRate

- [ ] 2.2.1 Verify/add `@MapperAuto` to `ExchangeRateDto`
- [ ] 2.2.2 Fix 1 `toBuilder()` site in `ExchangeRateService` to use `applyUpdate()`
- [ ] 2.2.3 Refactor `ExchangeRateController` to RESTful endpoints, add `@RequiredArgsConstructor`
- [ ] 2.2.4 Add nested `record` request DTOs to controller
- [ ] 2.2.5 Write 5-7 unit tests in `ExchangeRateServiceTest`
- [ ] 2.2.6 Write 4-6 integration tests in `ExchangeRateControllerIntegrationTest`
- [ ] 2.2.7 Run live API test: count-before/after
- [ ] 2.2.8 Commit and push branch

### 2.3 InsightsExplorer

- [ ] 2.3.1 Verify/add `@MapperAuto` to `InsightsExplorerDto`
- [ ] 2.3.2 Fix 3 `toBuilder()` sites in `InsightsExplorerService` to use `applyUpdate()`
- [ ] 2.3.3 Refactor `InsightsExplorerController` to RESTful endpoints, add `@RequiredArgsConstructor`
- [ ] 2.3.4 Add nested `record` request DTOs to controller
- [ ] 2.3.5 Write 6-8 unit tests in `InsightsExplorerServiceTest`
- [ ] 2.3.6 Write 5-7 integration tests in `InsightsExplorerControllerIntegrationTest`
- [ ] 2.3.7 Run live API test: count-before/after
- [ ] 2.3.8 Commit and push branch

### 2.4 TagGroup

- [ ] 2.4.1 Verify/add `@MapperAuto` to `TagGroupDto`
- [ ] 2.4.2 Audit `TagService.createTagGroup()`/`updateTagGroup()` for `toBuilder()` (currently 0 sites)
- [ ] 2.4.3 Split `TagController` to add separate `/tag-groups` endpoints, add `@RequiredArgsConstructor`
- [ ] 2.4.4 Add nested `record` request DTOs to controller
- [ ] 2.4.5 Write 3-4 unit tests in `TagServiceTest` (or new `TagGroupServiceTest`)
- [ ] 2.4.6 Write 3-4 integration tests in `TagControllerIntegrationTest` (or new `TagGroupControllerIntegrationTest`)
- [ ] 2.4.7 Run live API test: count-before/after
- [ ] 2.4.8 Commit and push branch

### Batch 1 Final

- [ ] 2.99.1 Run all unit tests (`./gradlew test`)
- [ ] 2.99.2 Run all integration tests (`./gradlew integrationTest`)
- [ ] 2.99.3 Open PR for batch 1
- [ ] 2.99.4 Merge batch 1 to main
- [ ] 2.99.5 Clean up worktree

---

## 3. Batch 2: Tag (depends on TagGroup)

### 3.1 Tag

- [ ] 3.1.1 Verify `@MapperAuto` on `TagDto` covers all fields
- [ ] 3.1.2 Fix 6 `toBuilder()` sites in `TagService` to use `applyUpdate()`
- [ ] 3.1.3 Rewrite `TagController`: remove all 11 legacy `.json` endpoints, use RESTful
- [ ] 3.1.4 Add nested `record` request DTOs (`TagCreateRequest`, `TagUpdateRequest`, `TagHideRequest`)
- [ ] 3.1.5 Add `@RequiredArgsConstructor` to `TagController`
- [ ] 3.1.6 Write 8-10 unit tests in `TagServiceTest` (use real `TagDtoMapperConverter`, `@Nested` classes)
- [ ] 3.1.7 Update `TagControllerIntegrationTest` to use RESTful paths
- [ ] 3.1.8 Add 8-10 integration tests including PATCH for hide/unhide
- [ ] 3.1.9 Add count-before/after regression test for reorder/update
- [ ] 3.1.10 Run live API test
- [ ] 3.1.11 Commit and push branch

### Batch 2 Final

- [ ] 3.99.1 Run all unit + integration tests
- [ ] 3.99.2 Open PR, merge to main
- [ ] 3.99.3 Clean up worktree

---

## 4. Batch 3: Account (heavily used, no internal deps)

### 4.1 Account

- [ ] 4.1.1 Verify `@MapperAuto` on `AccountDto` covers all fields (including computed balance)
- [ ] 4.1.2 Fix 7 `toBuilder()` sites in `AccountService` to use `applyUpdate()`
- [ ] 4.1.3 Rewrite `AccountController`: remove all 9 legacy `.json` endpoints, use RESTful
- [ ] 4.1.4 Add nested `record` request DTOs (`AccountCreateRequest`, `AccountUpdateRequest`, `AccountHideRequest`)
- [ ] 4.1.5 Add `@RequiredArgsConstructor` to `AccountController`
- [ ] 4.1.6 Update `AccountServiceTest` to use real `AccountDtoMapperConverter` and add 8-10 more tests
- [ ] 4.1.7 Update `AccountControllerIntegrationTest` to use RESTful paths
- [ ] 4.1.8 Add 8-10 integration tests including PATCH for hide/unhide
- [ ] 4.1.9 Add count-before/after regression test
- [ ] 4.1.10 Handle special cases: transfer flow, balance updates, soft delete
- [ ] 4.1.11 Run live API test
- [ ] 4.1.12 Commit and push branch

### Batch 3 Final

- [ ] 4.99.1 Run all tests
- [ ] 4.99.2 Open PR, merge to main
- [ ] 4.99.3 Clean up worktree

---

## 5. Batch 4: Budget, TransactionTemplate

### 5.1 Budget

- [ ] 5.1.1 Add `@MapperAuto` to `BudgetDto` (may need creation)
- [ ] 5.1.2 Fix 1 `toBuilder()` site in `BudgetService` to use `applyUpdate()`
- [ ] 5.1.3 Add `@RequiredArgsConstructor` to `BudgetController` (currently uses manual constructor)
- [ ] 5.1.4 Add nested `record` request DTOs (`BudgetCreateRequest`, `BudgetUpdateRequest`)
- [ ] 5.1.5 Verify all endpoints are RESTful (no `.json`)
- [ ] 5.1.6 Write 5-7 unit tests in `BudgetServiceTest`
- [ ] 5.1.7 Write 5-7 integration tests in `BudgetControllerIntegrationTest`
- [ ] 5.1.8 Add count-before/after regression test
- [ ] 5.1.9 Run live API test
- [ ] 5.1.10 Commit and push branch

### 5.2 TransactionTemplate

- [ ] 5.2.1 Add `@MapperAuto` to `TransactionTemplateDto`
- [ ] 5.2.2 Fix 3 `toBuilder()` sites in `TransactionTemplateService` to use `applyUpdate()`
- [ ] 5.2.3 Rewrite `TransactionTemplateController` to RESTful endpoints, add `@RequiredArgsConstructor`
- [ ] 5.2.4 Add nested `record` request DTOs
- [ ] 5.2.5 Write 5-7 unit tests in `TransactionTemplateServiceTest`
- [ ] 5.2.6 Write 5-7 integration tests in `TransactionTemplateControllerIntegrationTest`
- [ ] 5.2.7 Add count-before/after regression test
- [ ] 5.2.8 Run live API test
- [ ] 5.2.9 Commit and push branch

### Batch 4 Final

- [ ] 5.99.1 Run all tests
- [ ] 5.99.2 Open PR, merge to main
- [ ] 5.99.3 Clean up worktree

---

## 6. Batch 5: TransactionPicture, ScheduledTransaction, Transaction

### 6.1 TransactionPicture

- [ ] 6.1.1 Add `@MapperAuto` to `TransactionPictureDto`
- [ ] 6.1.2 Audit `TransactionPictureService` for `toBuilder()` (currently 0 sites)
- [ ] 6.1.3 Rewrite `TransactionPictureController` to RESTful endpoints, add `@RequiredArgsConstructor`
- [ ] 6.1.4 Add nested `record` request DTOs
- [ ] 6.1.5 Write 3-5 unit tests in `TransactionPictureServiceTest`
- [ ] 6.1.6 Write 3-5 integration tests in `TransactionPictureControllerIntegrationTest`
- [ ] 6.1.7 Run live API test
- [ ] 6.1.8 Commit and push branch

### 6.2 ScheduledTransaction

- [ ] 6.2.1 Add `@MapperAuto` to `ScheduledTransactionDto`
- [ ] 6.2.2 Fix 1 `toBuilder()` site in `ScheduledTransactionService` to use `applyUpdate()`
- [ ] 6.2.3 Rewrite `ScheduledTransactionController` to RESTful endpoints, add `@RequiredArgsConstructor`
- [ ] 6.2.4 Add nested `record` request DTOs
- [ ] 6.2.5 Write 5-7 unit tests in `ScheduledTransactionServiceTest`
- [ ] 6.2.6 Write 5-7 integration tests in `ScheduledTransactionControllerIntegrationTest`
- [ ] 6.2.7 Add count-before/after regression test
- [ ] 6.2.8 Run live API test
- [ ] 6.2.9 Commit and push branch

### 6.3 Transaction (most complex, last)

- [ ] 6.3.1 Verify `@MapperAuto` on `TransactionDto` covers all fields
- [ ] 6.3.2 Fix 8 `toBuilder()` sites in `TransactionService` to use `applyUpdate()` (highest count!)
- [ ] 6.3.3 Rewrite `TransactionController`: remove all 8 legacy `.json` endpoints, use RESTful
- [ ] 6.3.4 Add nested `record` request DTOs (`TransactionCreateRequest`, `TransactionUpdateRequest`)
- [ ] 6.3.5 Add `@RequiredArgsConstructor` to `TransactionController`
- [ ] 6.3.6 Write 12-15 unit tests in `TransactionServiceTest` (lots of business logic, transfers)
- [ ] 6.3.7 Write 12-15 integration tests in `TransactionControllerIntegrationTest`
- [ ] 6.3.8 Add count-before/after regression tests for all update paths
- [ ] 6.3.9 Special: extra tests for transfer flow (creates 2 linked records)
- [ ] 6.3.10 Special: extra tests for relatedId setting
- [ ] 6.3.11 Special: extra tests for balance updates
- [ ] 6.3.12 Run live API test
- [ ] 6.3.13 Commit and push branch

### Batch 5 Final

- [ ] 6.99.1 Run all tests
- [ ] 6.99.2 Open PR, merge to main
- [ ] 6.99.3 Clean up worktree

---

## 7. Frontend Updates (After All Backend Batches)

> **Strategy**: Update frontend per batch, not all at end. Each backend batch's frontend update happens immediately after that batch merges.

### 7.1 Frontend: Batch 1 Models (Token, ExchangeRate, InsightsExplorer, TagGroup)

- [ ] 7.1.1 Update any pages calling Token API
- [ ] 7.1.2 Update any pages calling ExchangeRate API
- [ ] 7.1.3 Update any pages calling InsightsExplorer API
- [ ] 7.1.4 Update any pages calling TagGroup API
- [ ] 7.1.5 Verify envelope unwrapping in all updates

### 7.2 Frontend: Tag

- [ ] 7.2.1 Update `pages/tags.vue` (if exists) to use RESTful API
- [ ] 7.2.2 Remove `.json` from any path
- [ ] 7.2.3 Use `api.patch()` for hide/unhide
- [ ] 7.2.4 Verify envelope unwrapping

### 7.3 Frontend: Account

- [ ] 7.3.1 Update `pages/accounts.vue` to use RESTful API
- [ ] 7.3.2 Remove `.json` from paths
- [ ] 7.3.3 Use `api.patch()` for hide/unhide
- [ ] 7.3.4 Verify envelope unwrapping

### 7.4 Frontend: Budget

- [ ] 7.4.1 Update `pages/budgets.vue` to use RESTful API

### 7.5 Frontend: TransactionTemplate

- [ ] 7.5.1 Update `pages/transaction-templates.vue` to use RESTful API

### 7.6 Frontend: Transaction (largest, last)

- [ ] 7.6.1 Update `pages/transactions.vue` to use RESTful API
- [ ] 7.6.2 Remove all 8 `.json` paths
- [ ] 7.6.3 Use `api.patch()` for any state toggles
- [ ] 7.6.4 Fix envelope unwrapping
- [ ] 7.6.5 Update transfer flow to use new POST endpoint (handles transfers automatically)
- [ ] 7.6.6 Update picture upload component to use RESTful path
- [ ] 7.6.7 Update LLM receipt recognition to use RESTful path

### 7.7 Frontend: TransactionPicture

- [ ] 7.7.1 Update picture upload UI to use RESTful path
- [ ] 7.7.2 Update picture list rendering

### 7.8 Frontend: ScheduledTransaction

- [ ] 7.8.1 Update `pages/scheduled.vue` to use RESTful API

### Frontend Final

- [ ] 7.99.1 Run `nuxt prepare` to verify no type errors
- [ ] 7.99.2 Run `nuxt dev` and verify all pages work
- [ ] 7.99.3 Update `composables/useApi.ts` if any new methods needed
- [ ] 7.99.4 Update `AGENTS.md` frontend section

---

## 8. Post-Refactor Tasks

- [ ] 8.1 Run full test suite: `./gradlew test integrationTest` — verify all 200+ tests pass
- [ ] 8.2 Run live API tests for all 12 models — verify count-before/after for every update path
- [ ] 8.3 Open OpenSpec verify: `npx openspec verify model-refactor-canonical-pattern`
- [ ] 8.4 Sync specs to main: `npx openspec sync-specs model-refactor-canonical-pattern`
- [ ] 8.5 Update `AGENTS.md` to reference the canonical pattern as the rule for all new models
- [ ] 8.6 Update `docs/refactoring/REFACTORING-ANALYSIS.md` (or delete it as superseded)
- [ ] 8.7 Update `docs/USER-GUIDE.md` if any public API changes affect user docs
- [ ] 8.8 Final commit and merge to main
- [ ] 8.9 Archive this OpenSpec change: `npx openspec archive model-refactor-canonical-pattern`
- [ ] 8.10 (Optional) Add pre-commit hook to detect bare `toBuilder()` in services

---

## 9. Completion Criteria

This change is **complete** when:

- [ ] All 12 models use the canonical pattern (entity, DTO, service, controller, tests)
- [ ] All 30 `toBuilder()` sites replaced with `applyUpdate()`
- [ ] All legacy `.json` endpoints removed
- [ ] All controllers use `@RequiredArgsConstructor`
- [ ] All models have `@MapperAuto` on DTOs
- [ ] All models have unit tests (use real mapper)
- [ ] All models have integration tests (Apache HttpClient5)
- [ ] All count-before/after regression tests pass
- [ ] All frontend pages use RESTful APIs
- [ ] All OpenSpec specs synced to main
- [ ] `AGENTS.md` updated
- [ ] All changes committed and pushed to GitHub
- [ ] OpenSpec change archived

---

## Notes

- **Reference**: See `docs/refactoring/MODEL-REFACTOR-PLAN.md` for the full plan
- **Reference**: See `.pi/skills/bookkeeping-refactor/SKILL.md` for the canonical pattern
- **Reference**: See `openspec/changes/archive/2026-06-08-category-api-restructure/` for the proven pattern
- **Apply phase**: Run `npx openspec apply model-refactor-canonical-pattern` to start implementing
