# Frontend Enhancement Plan

## Current State
- 13 Vue pages using Nuxt 4 + Vuetify 3
- All pages have basic CRUD but missing key features

## Plan

### Sprint 1: Templates + Profile (P0)
1. [x] `pages/templates.vue` — Transaction template CRUD (CREATE)
2. [ ] `pages/profile.vue` — Full user settings with all 12 fields + avatar + data management

### Sprint 2: Tags Enhancement + Batch Ops (P0)
3. [ ] Tags: add tag groups sidebar, batch tag operations in transactions
4. [ ] Transactions: add batch delete, batch category/account change

### Sprint 3: Statistics + Reports (P1)
5. [ ] Statistics: add trend charts (line/bar), asset trends
6. [ ] Reports: add reconciliation statement, cash flow

### Sprint 4: UI Polish (P1)
7. [ ] Import/export buttons in transactions
8. [ ] Token management UI in profile
9. [ ] Exchange rates page (basic)

## Pages to Create/Update

| Page | Status | Actions |
|------|--------|---------|
| `templates.vue` | ❌ Missing | CREATE |
| `profile.vue` | ⚠️ Partial | ENHANCE |
| `tags.vue` | ⚠️ Basic | ENHANCE |
| `transactions.vue` | ⚠️ Basic | ENHANCE |
| `statistics.vue` | ⚠️ Basic | ENHANCE |
| `reports.vue` | ⚠️ Basic | ENHANCE |
| `exchange.vue` | ❌ Missing | CREATE |

## Backend Endpoints Used
- Templates: list, create, update, delete, batch operations
- Profile: get/update profile, avatar upload, data statistics
- Tags: CRUD + tag groups
- Transactions: batch operations
- Statistics: trends, asset trends, reconciliation
- Data: export, clear
- Tokens: list, generate, revoke