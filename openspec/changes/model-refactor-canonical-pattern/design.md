## Context

The Category module was the first to be refactored to a canonical pattern (see `openspec/changes/archive/2026-06-08-category-api-restructure/`). During that refactor, a critical bug was discovered: every service that uses `entity.toBuilder()...build()` followed by `save()` creates a **duplicate row** instead of updating the existing one, because Lombok's `@Builder(toBuilder = true)` does not copy inherited fields (`id`, `createdAt`) from the parent `BaseEntity`.

This bug affects 30 call sites across 9 services beyond Category: `AccountService` (7), `TagService` (6), `TransactionService` (8), `InsightsExplorerService` (3), `TransactionTemplateService` (3), `ExchangeRateService` (1), `BudgetService` (1), `ScheduledTransactionService` (1). The fix `BaseEntity.applyUpdate(Function)` is already in place (from the Category refactor) — it explicitly preserves `id` and `createdAt` after the updater runs.

Beyond the bug, models are inconsistent: legacy `.json` endpoints, manual constructors, missing DTOs, missing tests. The Category refactor established a 9-step canonical pattern; this change applies it systematically to all 12 models, ordered by dependency.

## Goals / Non-Goals

**Goals:**

- Eliminate the `toBuilder()` inherited-id bug across all 9 affected services (30 sites)
- Apply the canonical pattern to all 12 entity models, ordered by dependency (least-dependent first)
- Migrate all 8 controllers to RESTful endpoints (no `.json` suffix)
- Add `@MapperAuto` DTO coverage for all models
- Add unit + integration tests for all 12 models (real `XxxDtoMapperConverter`, Apache HttpClient5 for PATCH)
- Update all frontend pages to use new RESTful APIs and fix envelope-unwrapping bugs
- Establish a verified, repeatable process so future models follow the same pattern

**Non-Goals:**

- Adding new functionality or new API endpoints (pure refactor)
- Changing the public API contract beyond what Category already established
- Database schema changes (no new fields, no new tables)
- Performance optimization (we're not changing query patterns)
- Authentication/authorization changes (the per-user pattern is already in place)
- Migrating data from old to new (no data migration needed — same data, cleaner code)

## Decisions

### Decision 1: Dependency-ordered batches (not parallel or random)

**Choice**: Refactor in 5 batches by dependency (Batch 1: Token/ExchangeRate/InsightsExplorer/TagGroup → Batch 2: Tag → Batch 3: Account → Batch 4: Budget/TransactionTemplate → Batch 5: TransactionPicture/ScheduledTransaction/Transaction).

**Rationale**:
- Refactoring a model in isolation is faster than refactoring it after its dependents change
- Each batch can be merged to main before the next starts, reducing merge conflicts
- Early batches (Token, ExchangeRate) are small (1-3 sites), proving the pattern before tackling complex ones
- Tag must come after TagGroup; Account must come before Transaction; Transaction must come last

**Alternatives considered**:
- *All at once*: rejected — too many concurrent changes, hard to review, hard to bisect bugs
- *By complexity*: rejected — TagGroup has 0 toBuilder sites but is a dependency for Tag
- *By model size*: rejected — small models often have tricky dependencies (Tag is small but is used by Transaction)

### Decision 2: Per-batch git worktree, not single shared branch

**Choice**: Each batch lives in its own git worktree and branch (`refactor/batch-N-models`). Merge to main after each batch.

**Rationale**:
- Working on 5 batches in one branch means 5 merge conflicts
- Per-batch worktree allows easy rollback if a batch goes wrong
- Code review is more focused (one batch per PR)
- Tests can run independently per batch

**Alternatives considered**:
- *Single branch with frequent rebase*: rejected — rebase pain increases with batch count
- *Monorepo without branches*: rejected — no isolation, no rollback

### Decision 3: Use the existing `BaseEntity.applyUpdate()` fix, don't re-architect

**Choice**: Every `toBuilder()...build()` call becomes `applyUpdate(c -> c.toBuilder()...build())`. No changes to `BaseEntity`, no new abstractions.

**Rationale**:
- The fix is already proven (Category passes 28 unit + 17 integration tests)
- It's a minimal-diff change (just wrap the existing lambda)
- It preserves the existing `toBuilder()` pattern (no rewriting needed)
- It documents the bug at every call site (the lambda is visible)

**Alternatives considered**:
- *Rewrite with MapStructPlus update mappers*: rejected — adds 12 new mappers, more code, more learning
- *Add field-by-field setters (forbidden by AGENTS.md)*: rejected — violates project conventions
- *Use `@SuperBuilder` from Lombok*: rejected — would require changing 13 entities and breaks existing tests

### Decision 4: Tests use the real `XxxDtoMapperConverter`, not a mock

**Choice**: Unit tests instantiate `new XxxDtoMapperConverter()` and use it directly. Mappers are not mocked.

**Rationale**:
- The converter is auto-generated and free; mocking it adds complexity without value
- Using the real converter catches mapping bugs (e.g., missed field, wrong type conversion)
- It documents the expected behavior — if the converter changes, tests catch it
- Established by Category refactor and works well there

**Alternatives considered**:
- *Mock the mapper*: rejected — adds test maintenance, hides mapping bugs
- *Skip mapper tests*: rejected — allows silent mapping regressions

### Decision 5: Tests use `@Nested` classes for organization, not flat test methods

**Choice**: Each public service method gets a `@Nested` class containing its tests. Method names describe the scenario.

**Rationale**:
- Clear visual grouping by method (easy to find tests for `updateXxx()`)
- `@DisplayName` allows long, descriptive names
- Establishes consistency with the Category service test (28 tests, 8 nested classes)

**Alternatives considered**:
- *Flat methods with prefixes*: rejected — less readable, harder to navigate
- *One test class per method*: rejected — too many files, harder to find helpers

### Decision 6: RESTful endpoint design follows strict conventions

**Choice**:
- Plural noun in path: `/api/v1/categories` (not `/category`)
- HTTP verb for action: `GET`/`POST`/`PUT`/`PATCH`/`DELETE`
- No `.json` suffix anywhere
- IDs in path: `/api/v1/categories/{id}`
- Sub-resources: `/api/v1/categories/{id}/pictures`
- Special actions: `/api/v1/categories/reorder` (verb on collection, not single resource)
- PATCH for partial state changes: `/api/v1/categories/{id}/hidden`

**Rationale**:
- Industry standard (REST)
- Predictable for frontend (no guessing `.json` vs no suffix)
- Clean separation: list/create on collection, get/update/delete on resource
- Matches Category refactor exactly

**Alternatives considered**:
- *Keep `.json` for backwards compat*: rejected — no external clients (frontend is internal)
- *Use RPC-style `/api/v1/getCategory`*: rejected — not RESTful
- *Use verbs everywhere*: rejected — anti-pattern

### Decision 7: Frontend updates happen after each backend batch, not all at end

**Choice**: After each backend batch is merged, update the corresponding frontend pages. Don't wait for all 5 backend batches.

**Rationale**:
- Frontend and backend can be tested together immediately
- No big-bang frontend refactor at the end (hard to debug)
- Each batch's frontend change is small and reviewable
- Matches `frontend-hybrid-redesign` OpenSpec change which is in progress

**Alternatives considered**:
- *All backend first, then all frontend*: rejected — frontend broken during entire backend phase
- *Frontend per-model in same PR*: rejected — PR too large, hard to review

### Decision 8: Skip OpenSpec specs for the other 11 models (not create new spec files for each)

**Choice**: Create ONE spec file `model-canonical-pattern/spec.md` that defines the pattern. Don't create spec files for Token, Account, etc.

**Rationale**:
- The Category specs (category-crud, category-icon-color, category-transfer-type) describe *what* Category does
- The other 11 models don't have specs — and creating 11 new specs is out of scope (that's a separate "document the system" effort)
- The `model-canonical-pattern` spec describes *how* all models are built
- This keeps the change focused on refactoring, not on documentation

**Alternatives considered**:
- *Create 11 new spec files*: rejected — massive scope creep, not part of "refactor"
- *Skip specs entirely*: rejected — without specs, no acceptance criteria for "done"
- *Create one mega-spec per model*: rejected — duplicates the canonical pattern

## Risks / Trade-offs

- **Risk**: Refactoring 12 models touches a lot of code → **Mitigation**: Per-batch worktree + per-batch PR = small, reviewable changes
- **Risk**: The `applyUpdate()` fix might miss edge cases (e.g., collections, relationships) → **Mitigation**: Count-before/after live API test on every model; integration tests for every model
- **Risk**: Transaction is very complex (transfers, relatedId, balance updates) → **Mitigation**: Allocate 6-8 hours for Transaction alone; write extra tests for transfer flow
- **Risk**: Frontend-backend desync during the multi-batch process → **Mitigation**: Frontend updates happen immediately after each backend batch
- **Risk**: Data corruption during refactor (duplicate rows accumulating) → **Mitigation**: Clean up dev DB before starting; run live tests after each batch
- **Risk**: Lombok regeneration behavior on different JDK versions → **Mitigation**: Existing Category refactor works on JDK 25; same setup
- **Risk**: Merge conflicts with `frontend-hybrid-redesign` change (in progress) → **Mitigation**: Coordinate with that change; consider rebasing or merging carefully

## Migration Plan

The migration is a rolling refactor, not a one-time event. Each batch is independent.

**Pre-flight (before any batch)**:
1. Clean up dev database duplicates from the existing bug (manual SQL or `data clear` endpoint)
2. Snapshot dev DB (`pg_dump bookkeeping_dev > pre-refactor-backup.sql`)
3. Confirm all current tests pass (baseline)

**Per batch**:
1. Create git worktree: `git worktree add ../bookkeeping-batch-N -b refactor/batch-N-models`
2. In worktree, refactor each model in the batch
3. Run unit + integration tests
4. Run live API test (count-before/after) for each refactored model
5. Update frontend pages for refactored models
6. Commit per model, push branch
7. Open PR per batch
8. After PR review, merge to main
9. Update main OpenSpec specs (sync from delta if needed)
10. Move on to next batch

**Rollback strategy**:
- If a batch fails tests, fix in the same branch
- If a batch breaks production behavior, revert the merge commit (revert is clean because batches are isolated)
- If catastrophic, restore DB from snapshot

**Post-completion**:
1. Archive this OpenSpec change (`openspec archive model-refactor-canonical-pattern`)
2. Update `AGENTS.md` with the canonical pattern as the rule for all future models
3. Delete or deprecate the older `REFACTORING-ANALYSIS.md` (2026-05-22)

## Open Questions

- **Q1**: Should we add a pre-commit hook that rejects bare `toBuilder()` in services? — *Decision deferred; will add as follow-up if pattern recurs*
- **Q2**: Should we refactor `McPController` and other supporting controllers in this change? — *Decision: No, this change is scoped to the 12 entity models; other controllers are out of scope*
- **Q3**: Should we add a `data-initialization` change to clean up the duplicate rows? — *Decision: Cleanup happens as part of this refactor (run `data clear` endpoint per batch); no separate change needed*
- **Q4**: Frontend `transactions.vue` is very large (uses LLM, picture upload, transfer logic) — should we refactor it in one go or break it up? — *Decision: Refactor in one go but with careful testing; the page is already large and works, we're just changing API calls*
