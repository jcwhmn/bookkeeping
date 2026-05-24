# Frontend Enhancement Plan ✅

## Completed

### Sprint 1: Templates + Profile (P0)
1. [x] `pages/templates.vue` — Transaction template CRUD
2. [x] `pages/profile.vue` — Full user settings (Basic/Security/Tokens/Data tabs)

### Sprint 2: Tags Enhancement + Batch Ops (P0)
3. [x] `pages/tags.vue` — Tag groups sidebar, batch tag operations
4. [x] `pages/transactions.vue` — Batch delete, batch category/account change

### Sprint 3: Statistics + Reports (P1)
5. [x] `pages/statistics.vue` — Trend charts (line/bar), asset trends
6. [x] `pages/reports.vue` — Reconciliation statement, cash flow

### Sprint 4: Exchange Rates + Import/Export
7. [x] `pages/exchange.vue` — Exchange rates with base currency, custom rates
8. [x] `pages/transactions.vue` — Export dropdown (CSV/TSV), import dialog

### Sprint 5: 2FA + Insights + LLM
9. [x] `pages/profile.vue` — 2FA setup with QR/secret, recovery codes, OAuth2 connect
10. [x] `pages/insights.vue` — Query builder, chart/table views, save/load/hide/delete queries
11. [x] `pages/transactions.vue` — AI Scan button + LLM receipt dialog

## Summary
- **16 Vue pages** (was 13, added templates + exchange + insights)
- **Enhanced 8 pages**: profile (2FA/OAuth), tags, transactions (batch/import/export/LLM), statistics, reports
- **Batch operations**: checkbox multi-select, batch dialogs, batch delete
- **3-mode statistics**: categorical, trends, asset trends
- **3-tab reports**: summary, cash flow, reconciliation
- **2FA**: setup dialog with QR, confirmation, recovery codes
- **Insights Explorer**: 5 dimensions, filters, chart/table, save/load
- **LLM**: receipt scan button + dialog in transactions