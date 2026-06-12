## Why

The Category module refactor (change `category-api-restructure`, archived 2026-06-08) exposed a **critical bug** in every entity that extends `BaseEntity`: Lombok's `@Builder(toBuilder = true)` does not include inherited fields like `id` and `createdAt`, so `entity.toBuilder()...build()` produces a new entity with `id=null` that Hibernate INSERTs as a duplicate row. The dev database had 50+ duplicate categories before the fix. This same bug exists in 30 call sites across 9 other services (Account, Tag, Transaction, Budget, ExchangeRate, InsightsExplorer, ScheduledTransaction, TransactionTemplate), causing the same data corruption invisibly. Beyond the bug, the 12 models are inconsistent: some have legacy `.json`-suffixed endpoints, some use manual constructors, some lack DTOs/tests, and some are still using setters. We need a single canonical pattern, proven by Category, applied systematically to all models.

## What Changes

- **Fix the `toBuilder()` inherited-id bug** across all 9 affected services by replacing `toBuilder()...build()` with `applyUpdate(c -> c.toBuilder()...build())`
- **Apply the canonical pattern** (established by Category refactor) to all 12 entity models:
  - Token, ExchangeRate, InsightsExplorer, TagGroup, Tag, Account, Budget, TransactionTemplate, TransactionPicture, ScheduledTransaction, Transaction
- **Migrate to RESTful endpoints** for all controllers (remove `.json` suffix, use proper HTTP verbs, use `@RequiredArgsConstructor`)
- **Add missing DTOs with `@MapperAuto`** for models that lack them
- **Add comprehensive tests** (unit + integration) following the Category test pattern (real mapper converter, Apache HttpClient5 for PATCH)
- **Update frontend pages** to use new RESTful APIs (remove `.json` suffix, fix envelope unwrapping, use PATCH for hide/unhide)
- **BREAKING**: All legacy `.json` endpoints are removed in favor of RESTful ones. Frontend must be updated to use new paths.

## Capabilities

### New Capabilities

- `model-canonical-pattern`: Define the canonical entity/service/controller/DTO/test pattern that all models SHALL follow. Includes the 9-step refactor process, the `toBuilder()` bug fix via `BaseEntity.applyUpdate()`, and verification procedures.

### Modified Capabilities

(none — existing specs are the *target* of the refactor, not the source. The category-crud, category-icon-color, category-transfer-type, and design-system specs already describe the desired state. The other 11 models do not yet have specs, so they fall under the new "model-canonical-pattern" capability.)

## Impact

- **Backend (Java/Spring Boot)**:
  - 12 entity models in `core/` and `supporting/` packages
  - 12 services in same packages
  - 8 controllers in `core/`
  - 1 shared base class (`BaseEntity.java`)
  - ~30 bug sites in 9 services
  - ~50+ new tests (unit + integration)
- **Frontend (Nuxt 4)**:
  - ~6 pages in `pages/` directory
  - 1 shared composable (`composables/useApi.ts`)
  - 1 config file (`nuxt.config.ts`)
- **Build/Config**:
  - `backend/build.gradle.kts` (already has httpclient5 dependency from Category refactor)
- **Docs**:
  - `AGENTS.md` (update with canonical pattern rules)
  - `docs/refactoring/MODEL-REFACTOR-PLAN.md` (already created)
  - New OpenSpec capability: `model-canonical-pattern`
- **Database**:
  - No schema changes required (we're not adding fields, just fixing behavior)
  - Existing dev/test databases may need cleanup of duplicate rows from the bug
