# Season 2 Development Workflow

## Overview

This document defines the **formal workflow** for all Season 2 development. Every feature goes through this pipeline: Discovery → Design → Implementation → Verification.

---

## The Pipeline

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  Discovery  │ →  │   Design   │ →  │Implement    │ →  │ Verification│
│  (Day 1-2)  │    │  (Day 3-5) │    │ (Day 6-14)  │    │  (Day 15+)  │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

---

## Phase 1: Discovery (1-2 days per feature)

### 1.1 — Bug Hunt
- [ ] Run all existing tests (`./gradlew test`)
- [ ] Manual smoke test all pages (login, dashboard, accounts, transactions, categories)
- [ ] Capture screenshots of UI bugs
- [ ] Document in `docs/bugs/SEASON2-BUGS.md`

### 1.2 — Tech Stack Audit
- [ ] Verify Spring Boot + Nuxt compatibility
- [ ] Check dependency versions (no conflicts)
- [ ] Verify PostgreSQL schema matches entities
- [ ] Check OpenAPI spec coverage for each feature
- [ ] Document any tech debt in `docs/tech-debt/`

### 1.3 — Feature Backlog Refinement
- [ ] Break each P1/P2/P3 feature into user stories
- [ ] Each story has: title, acceptance criteria, API contract, DB migration
- [ ] Prioritize by: user value, effort, dependencies
- [ ] Add to `docs/backlog/SEASON2-BACKLOG.md`

---

## Phase 2: Design (3-5 days per feature)

### 2.1 — Design Spec per Page

For every new or modified page, create a design spec:

```
docs/design/pages/
├── 07-transactions-edit.md
├── 08-transactions-transfer.md
├── 09-tags.md
├── 10-budget.md
├── 11-reports.md
└── 12-settings.md
```

Each design spec includes:
- **Page purpose** — what the page does
- **Wireframe** — ASCII or visual layout
- **Components** — all UI components with states
- **Data model** — API request/response shapes
- **API endpoints** — exact endpoints and payloads
- **i18n keys** — all text that needs translation
- **Edge cases** — empty, loading, error, boundary
- **Mobile** — responsive behavior

### 2.2 — API Contract Review

Before writing code:
- [ ] Confirm API endpoint path matches OpenAPI
- [ ] Confirm request/response DTO structure
- [ ] Confirm error codes
- [ ] Confirm transaction types (1=MODIFY, 2=INCOME, 3=EXPENSE, 4=TRANSFER_OUT, 5=TRANSFER_IN)

### 2.3 — Data Model & Migration

- [ ] New entity → Flyway migration (V4__new_entity.sql)
- [ ] New field → ALTER TABLE migration
- [ ] Soft delete convention for all delete operations

---

## Phase 3: Implementation

### Rule: Backend First

**Always implement backend before frontend.** The API contract drives the UI.

### 3.1 — Backend Implementation Order

For each feature:
```
1. Entity (JPA class + migration)
2. Repository (Spring Data JPA)
3. DTO (Request + Response records)
4. Service (business logic)
5. Controller (REST endpoint + Swagger)
6. Exception handling (error codes)
7. Unit tests (plain JUnit, no Spring context)
8. Integration tests (@SpringBootTest)
```

### 3.2 — Frontend Implementation Order

```
1. API composable (useApi.ts update)
2. Page layout (wireframe)
3. Components (dialog, form, table)
4. State (Pinia store if needed)
5. i18n keys (en-US.json, zh-CN.json)
```

### 3.3 — Code Standards

| Rule | Details |
|------|---------|
| Amounts | Always BIGINT (cents), frontend divides by 100 |
| Timestamps | Always Unix epoch seconds (BIGINT) |
| Soft delete | `deleted` flag, never physical delete |
| Response | `{success, result, errorCode, errorMessage}` |
| Naming | PascalCase entities, camelCase DTOs, kebab-case files |
| Tests | 1 test class per service/controller, plain JUnit preferred |

---

## Phase 4: Verification

### 4.1 — Unit Tests
- [ ] Every service method has tests
- [ ] Every controller endpoint has integration tests
- [ ] Coverage target: 80%+

### 4.2 — Manual Test
- [ ] Execute TC from `docs/qa/TEST-CASES.md`
- [ ] Test on both Chrome and mobile viewport
- [ ] Sign off with tester signature

### 4.3 — Smoke Test Checklist

```
□ Login/Register flow works
□ Dashboard loads with real data
□ Create account → appears in list
□ Create transaction → updates account balance
□ Edit transaction → saves correctly
□ Delete transaction → reverts balance
□ Transfer → creates 2 linked transactions
□ Date filter → shows correct transactions
□ Tags → can add/remove from transaction
□ CSV export → downloads valid file
□ All pages render at 1920px and 375px
□ No console errors in browser
```

---

## Document Structure

```
docs/
├── design/
│   └── pages/
│       ├── 07-transactions-edit.md
│       ├── 08-transactions-transfer.md
│       ├── 09-tags.md
│       ├── 10-budget.md
│       ├── 11-reports.md
│       └── 12-settings.md
├── backlog/
│   └── SEASON2-BACKLOG.md
├── bugs/
│   └── SEASON2-BUGS.md
├── tech-debt/
│   └── TECHNICAL-DEBT.md
├── qa/
│   └── TEST-CASES.md
└── roadmap/
    └── SEASON2-ROADMAP.md
```

---

## Feature Implementation Order

Based on the OpenAPI analysis and existing code:

### Sprint 1 (Week 1-2): Transaction Foundation
1. **Bug Hunt** — find and fix existing bugs first
2. **Transaction Edit** — backend + frontend
3. **Transaction Delete** — backend + frontend with balance revert
4. **Transaction Date Picker** — backend accepts time, frontend calendar

### Sprint 2 (Week 3-4): Transfer + Month Navigation
5. **Transfer Support** — two-way transactions with `relatedId`
6. **Month Navigation** — prev/next month, month picker
7. **Search & Filter** — keyword, date range, amount range

### Sprint 3 (Week 5-6): Tags + Charts
8. **Tags Backend** — entity, CRUD, join table
9. **Tags Frontend** — tags page + chips in transactions
10. **Dashboard Charts** — real data, income vs expense bar chart

### Sprint 4 (Week 7-8): Reports + Budgets
11. **Transaction Statistics** — backend endpoint + frontend
12. **Budgets** — entity, CRUD, progress bars
13. **Monthly Reports Page** — summary + trends

### Sprint 5 (Week 9-10): Polish
14. **Account/Categ. Reorder** — drag & drop
15. **CSV Export** — download transactions as CSV
16. **User Settings Page** — language, currency, timezone
17. **Loading States + Toasts** — UX polish

---

## When to Do What

| Task | When | Output |
|------|------|--------|
| Bug Hunt | Day 1, before any code | `docs/bugs/SEASON2-BUGS.md` |
| Tech Stack Audit | Day 1 | `docs/tech-debt/TECHNICAL-DEBT.md` |
| Feature Spec | Before coding each feature | `docs/design/pages/XX-*.md` |
| API Contract | Before coding each feature | Inline in design spec |
| DB Migration | Before backend coding | `V4__*.sql` |
| Backend | Before frontend | Entity → Repo → Service → Controller |
| Frontend | After backend | Page by page |
| Tests | After each module | 80%+ coverage |
| Manual QA | After feature complete | Sign-off on test cases |

---

## Roles & Responsibilities

| Role | Responsibility |
|------|---------------|
| Developer | Implements backend + frontend, writes tests |
| Designer | Creates page design specs (can be developer) |
| QA | Writes test cases, executes manual tests |
| Tech Lead | Reviews PRs, approves API contracts, resolves conflicts |

---

## Definition of Done

A feature is **Done** when:
- [ ] Backend implemented and tested
- [ ] Frontend implemented and tested
- [ ] All unit tests pass (`./gradlew test`)
- [ ] Design spec created and approved
- [ ] Manual test cases executed and passed
- [ ] No known bugs in that feature
- [ ] i18n keys added for both languages

---

## Important Rules

1. **Never skip the design phase** — spec first, code second
2. **Backend before frontend** — API contract drives UI
3. **Test as you go** — don't leave testing to the end
4. **Migration files are sacred** — never modify old migrations
5. **Keep tests fast** — plain JUnit, no `@SpringBootTest` unless necessary
6. **Document decisions** — ADR for any architectural choice
7. **Small PRs** — one feature per PR, max 400 lines changed

---

## Communication

- Daily standup: what's done, what's next, blockers
- PR reviews: at least 1 reviewer approval
- Blocking issue → escalate immediately, don't wait

---

*Last updated: 2026-05-19*
*Season 2 | v0.2.0 target*