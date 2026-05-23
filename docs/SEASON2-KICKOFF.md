# Season 2 Kickoff — Bookkeeping v0.2.0

**Date**: 2026-05-22
**Target Version**: v0.2.0
**Duration**: ~10 weeks

---

## What We Did

### Phase 1: Discovery ✅

| Task | Output | Status |
|------|--------|--------|
| Bug Hunt | `docs/bugs/SEASON2-BUGS.md` | ✅ 3 bugs found, all fixed |
| Tech Stack Audit | `docs/tech-debt/TECHNICAL-DEBT.md` | ✅ No blockers |
| OpenAPI Analysis | `docs/roadmap/SEASON2-ROADMAP.md` | ✅ 14 features identified |

### Phase 2: Workflow ✅

| Document | Purpose |
|----------|---------|
| `docs/workflow/DEVELOPMENT-WORKFLOW.md` | Formal process for all features |
| `docs/backlog/SEASON2-BACKLOG.md` | 14 features with user stories |
| `docs/design/pages/07-transactions-edit.md` | Transaction edit/delete design |
| `docs/design/pages/08-transactions-transfer.md` | Transfer support design |
| `docs/design/pages/09-tags.md` | Tags management design |
| `docs/qa/TEST-CASES.md` | 72 test cases (manual + auto) |

---

## Current State

### Test Results
```
✅ 110 tests passing
✅ 0 P0 bugs remaining
✅ All previous features working
```

### What's Missing (P0)

| Feature | Description |
|---------|-------------|
| Transaction Edit | PUT /api/v1/transactions/{id} |
| Transaction Delete | DELETE /api/v1/transactions/{id} |
| Transaction Date Picker | Accept transactionTime |
| Transfer Support | Create two linked transactions |
| Month Navigation | Filter by year/month |

---

## Season 2 Scope

### Sprint 1 (Week 1-2): Transaction Foundation
```
FEATURE-TXN-001: Transaction Edit        (3 days)
FEATURE-TXN-002: Transaction Delete     (2 days)
FEATURE-TXN-003: Transaction Date Picker (2 days)
```

### Sprint 2 (Week 3-4): Transfer + Navigation
```
FEATURE-TXN-004: Transfer Support       (4 days)
FEATURE-TXN-005: Month Navigation       (3 days)
FEATURE-SEARCH-001: Search & Filter     (3 days)
```

### Sprint 3 (Week 5-6): Tags + Charts
```
FEATURE-TAGS-001: Tags System            (5 days)
FEATURE-STATS-001: Transaction Statistics (4 days)
FEATURE-CHART-001: Enhanced Dashboard   (4 days)
```

### Sprint 4 (Week 7-8): Reports + Polish
```
FEATURE-BUDGET-001: Budgets             (5 days)
FEATURE-REPORTS-001: Monthly Reports    (4 days)
FEATURE-REORDER-001: Account Reorder    (3 days)
```

### Sprint 5 (Week 9-10): Final Polish
```
FEATURE-EXPORT-001: CSV Export          (3 days)
FEATURE-SETTINGS-001: User Settings     (3 days)
FEATURE-UX-001: Loading + Toasts      (2 days)
FEATURE-UX-002: Confirm Dialogs        (2 days)
```

---

## Key Decisions Made

| Decision | Rationale |
|----------|-----------|
| Backend first | API contract drives UI |
| BIGINT for amounts | Avoid floating point issues |
| Unix timestamps | Consistent across stack |
| Soft delete for accounts | Recoverable, audit trail |
| Plain JUnit tests | Fast feedback (7s vs 20s) |
| No 2FA this season | Can add in v1.0 |
| MD5 password | Matches ezBookkeeping (compatibility) |

---

## Getting Started

### 1. Read the Workflow
```
docs/workflow/DEVELOPMENT-WORKFLOW.md
```

### 2. Pick a Feature
```
docs/backlog/SEASON2-BACKLOG.md
→ Find "📋 Todo" items
→ Check dependencies
```

### 3. Create Design Spec (if new page)
```
docs/design/pages/
→ Copy 07-transactions-edit.md as template
→ Fill in wireframe, components, API contract
```

### 4. Implement Backend First
```
Entity → Repository → Service → Controller → Tests
```

### 5. Implement Frontend
```
API composable → Page layout → Components → i18n
```

### 6. Verify
```
./gradlew test
→ Manual smoke test
→ Sign off on test cases
```

---

## Quick Commands

```bash
# Start backend
cd backend && ./gradlew bootRun --no-daemon

# Start frontend
cd frontend && npm run dev

# Run tests
cd backend && ./gradlew test

# Kill all processes
taskkill //F //IM java.exe
taskkill //F //IM node.exe
```

---

## Documents Reference

| Document | When to Use |
|----------|-------------|
| `DEVELOPMENT-WORKFLOW.md` | Every feature starts here |
| `SEASON2-BACKLOG.md` | Planning, prioritizing |
| `TEST-CASES.md` | Manual testing, QA sign-off |
| `SEASON2-BUGS.md` | Tracking known issues |
| `TECHNICAL-DEBT.md` | Architecture decisions |
| `SEASON2-ROADMAP.md` | High-level overview |
| `docs/design/pages/*.md` | Per-feature design specs |

---

## Team

| Role | Responsibility |
|------|----------------|
| Developer | Backend + Frontend + Tests |
| Designer | Page design specs |
| QA | Test execution, bug reports |

---

## Definition of Done

A feature is DONE when:
- [ ] Backend implemented & tested
- [ ] Frontend implemented & tested
- [ ] `./gradlew test` passes
- [ ] Design spec created
- [ ] Manual test cases passed
- [ ] No new bugs introduced

---

**Let's build v0.2.0!** 🚀