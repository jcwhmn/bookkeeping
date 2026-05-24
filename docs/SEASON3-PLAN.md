# Season 3 Planning — Feature Gaps vs ezBookkeeping Reference

**Reference**: `openapi.yaml` (112 endpoints) and `FRONTEND_PAGES.md` (30 pages, 2 UIs)

**Our current**: 30 endpoints across 9 controllers

## Gap Analysis — Endpoints by Category

| Category | ezBookkeeping (112) | We Have | Gap | Coverage |
|----------|-------------------|---------|-----|----------|
| **Auth** | 14 | 3 | 11 | 21% |
| **Users / Profile** | 8 | 3 | 5 | 38% |
| **Tokens** | 6 | 0 | 6 | 0% |
| **2FA** | 6 | 0 | 6 | 0% |
| **Data Management** | 6 | 0 | 6 | 0% |
| **Application Settings** | 3 | 0 | 3 | 0% |
| **External Auth (OAuth2)** | 6 | 0 | 6 | 0% |
| **Password Reset** | 2 | 0 | 2 | 0% |
| **Email Verification** | 2 | 0 | 2 | 0% |
| **Accounts** | 10 | 6 | 4 | 60% |
| **Transactions** | 32 | 9 | 23 | 28% |
| **Categories** | 8 | 3 | 5 | 38% |
| **Tags** | 10 | 6 | 4 | 60% |
| **Tag Groups** | 4 | 0 | 4 | 0% |
| **Templates** | 8 | 0 | 8 | 0% |
| **Insights / Explorers** | 7 | 0 | 7 | 0% |
| **Exchange Rates** | 3 | 0 | 3 | 0% |
| **Transaction Pictures** | 2 | 0 | 2 | 0% |
| **LLM / AI** | 1 | 0 | 1 | 0% |
| **System** | 3 | 2 | 1 | 67% |
| **Reconciliation** | 1 | 0 | 1 | 0% |
| **Total** | **112** | **~30** | **~82** | **~27%** |

---

## Season 3 Feature Priority

### 🔴 P0 — Critical (Foundation & Security)

#### S3-F1: Full Account Feature Set
**Why**: Two-level accounts (parent + sub-accounts), hide/show, reorder, move transactions between accounts
- Backend: sub-account delete, hide/unhide, reorder, move all transactions, transfer all transactions
- `POST /api/v1/accounts/move.json` — reorder accounts
- `POST /api/v1/accounts/hide.json` — hide/unhide account
- `POST /api/v1/accounts/sub_account/delete.json` — delete sub-account
- `POST /api/v1/accounts/delete.json` — delete (with sub-accounts)
- `POST /api/v1/transactions/move/all.json` — move all transactions between accounts
- Frontend: sub-account UI, hide toggle, drag-to-reorder, transfer all

#### S3-F2: Advanced Transaction Filtering
**Why**: Match reference feature completeness — cursor pagination, list by month, list all, count, amounts
- Backend:
  - `GET /transactions/list?max_time=&count=` — cursor pagination
  - `GET /transactions/list/by_month?year=&month=` — by month
  - `GET /transactions/list/all` — unpaginated for export
  - `GET /transactions/count` — total count
  - `GET /transactions/amounts` — aggregated income/expense for custom ranges
- Frontend: date range presets (today/yesterday/week/month/year/custom), amount range filter, list mode toggle

#### S3-F3: Batch Transaction Operations
**Why**: Bulk edit is essential for data cleanup
- Backend:
  - `POST /transactions/batch_update/category` — batch update category
  - `POST /transactions/batch_update/account` — batch update account
  - `POST /transactions/batch_update/tag/add` — batch add tags
  - `POST /transactions/batch_update/tag/remove` — batch remove tags
  - `POST /transactions/batch_update/tag/clear` — batch clear tags
  - `POST /transactions/batch_delete` — batch delete
  - `GET /transactions/count` — count for batch selection feedback
- Frontend: multi-select checkbox in transaction list, batch action toolbar

#### S3-F4: Data Management APIs
**Why**: Users need data stats, export, clear operations
- Backend:
  - `GET /api/v1/data/statistics` — count of all user data
  - `POST /api/v1/data/clear/all` — clear all user data
  - `POST /api/v1/data/clear/transactions` — clear all transactions
  - `POST /api/v1/data/clear/transactions/by_account` — clear by account
  - `GET /api/v1/data/export.csv` — full CSV export
  - `GET /api/v1/data/export.tsv` — TSV export
- Frontend: Data Management page (data stats, clear options, export)

#### S3-F5: User Profile (Extended)
**Why**: Complete profile management
- Backend:
  - `GET /api/v1/users/profile/get.json` — get full profile
  - `POST /api/v1/users/profile/update.json` — update profile (nickname, language, currency, etc.)
  - `POST /api/v1/users/avatar/update.json` — upload avatar
  - `POST /api/v1/users/avatar/remove.json` — remove avatar
- Frontend: Extended profile page (avatar upload, currency, language, week start, date format)

---

### 🟡 P1 — High Value (Advanced Features)

#### S3-F6: Transaction Templates
**Why**: Save time on repeated transactions
- Backend: Templates CRUD, save transaction as template, create from template
- `GET/POST /api/v1/transaction/templates/list.json`
- `GET /api/v1/transaction/templates/get.json`
- `POST /api/v1/transaction/templates/add.json`
- `POST /api/v1/transaction/templates/modify.json`
- `POST /api/v1/transaction/templates/hide.json`
- `POST /api/v1/transaction/templates/move.json`
- `POST /api/v1/transaction/templates/delete.json`
- Frontend: Templates page, create from template in transaction form

#### S3-F7: Enhanced Statistics
**Why**: Trend analysis, asset trends, amounts endpoint
- Backend:
  - `GET /api/v1/transactions/statistics/trends.json` — monthly/yearly trends
  - `GET /api/v1/transactions/statistics/asset_trends.json` — asset balance over time
  - `GET /api/v1/transactions/amounts.json` — custom time range aggregations
- Frontend: Trend charts (line/area), asset trend chart, configurable date ranges

#### S3-F8: Tag Groups
**Why**: Organize tags for better filtering
- Backend:
  - `GET /api/v1/transaction/tags/groups/list.json`
  - `POST /api/v1/transaction/tags/groups/add.json`
  - `POST /api/v1/transaction/tags/groups/modify.json`
  - `POST /api/v1/transaction/tags/groups/move.json`
  - `POST /api/v1/transaction/tags/groups/delete.json`
- Frontend: Tag group sidebar, group management dialog

#### S3-F9: Category Hierarchy (Enhancement)
**Why**: Parent-child categories (3 levels), batch add, hide/show, reorder
- Backend:
  - `POST /api/v1/transaction/categories/add_batch.json` — batch create
  - `POST /api/v1/transaction/categories/hide.json` — hide/unhide
  - `POST /api/v1/transaction/categories/move.json` — reorder
  - `GET /api/v1/transaction/categories/list.json?parent_id=` — list with parent filter
- Frontend: 3-level navigation, drag-to-reorder, hide toggle, preset category batch add

#### S3-F10: Reconciliation Statement
**Why**: Account reconciliation is a key feature for financial accuracy
- Backend: `GET /api/v1/transactions/reconciliation_statements.json`
  - Returns: transactions in range + opening/closing balance + total in/out
- Frontend: Reconciliation page (select account + date range, show statement)

---

### 🟢 P2 — Nice to Have (Advanced / AI)

#### S3-F11: Insights Explorers
**Why**: Custom multi-dimensional data analysis
- Backend: Save/query explorer configs (chart type, dimensions, filters)
- 7 endpoints for CRUD + hide + move on explorers
- Frontend: Insights explorer page (Query/Chart/Data Table/Editable tabs)

#### S3-F12: Import/Parse Transactions
**Why**: Import from CSV, OFX, QFX, QIF and other formats
- Backend: Parse custom file, parse standard formats, import, check import process
- 4 endpoints: parse_custom, parse_import, import, import_process
- Frontend: Import wizard (upload → column mapping → preview → confirm)

#### S3-F13: Transaction Pictures
**Why**: Attach receipts/invoices to transactions
- Backend:
  - `POST /api/v1/transaction/pictures/upload.json` — upload picture
  - `POST /api/v1/transaction/pictures/remove_unused.json` — cleanup
- Frontend: Picture attachment in transaction form

#### S3-F14: Exchange Rates
**Why**: Multi-currency support
- Backend:
  - `GET /api/v1/exchange_rates/latest.json` — latest rates
  - `POST /api/v1/exchange_rates/user_custom/update.json` — custom rates
  - `POST /api/v1/exchange_rates/user_custom/delete.json` — delete custom rate
- Frontend: Exchange rates page (table view, base currency switch, custom rates)

#### S3-F15: LLM Receipt Recognition
**Why**: AI-powered receipt scanning
- Backend: `POST /api/v1/llm/transactions/recognize_receipt_image.json`
- Frontend: Upload receipt image, parse transaction data
- (Requires LLM provider configuration)

#### S3-F16: Token Management (API/MCP)
**Why**: API access tokens, MCP tokens for AI integration
- Backend: Generate/revoke/list/refresh tokens
- Frontend: Security settings page (token list, generate, revoke)

---

## Season 3 Phases (Proposed)

### Phase S3-1: Foundation & Polish (P0)
- S3-F1: Full Account Feature Set
- S3-F2: Advanced Transaction Filtering
- S3-F3: Batch Transaction Operations
- S3-F4: Data Management APIs
- S3-F5: Extended User Profile

### Phase S3-2: Advanced Data Management (P1)
- S3-F6: Transaction Templates
- S3-F7: Enhanced Statistics (trends + assets)
- S3-F8: Tag Groups
- S3-F9: Category Hierarchy Enhancement
- S3-F10: Reconciliation Statement

### Phase S3-3: Enterprise Features (P2)
- S3-F11: Insights Explorers
- S3-F12: Import/Parse Transactions
- S3-F13: Transaction Pictures
- S3-F14: Exchange Rates
- S3-F15: LLM Receipt Recognition
- S3-F16: Token Management

---

## Summary

| Priority | Features | Endpoints | Effort |
|----------|----------|-----------|--------|
| **P0** | 5 features | ~30 endpoints | High |
| **P1** | 5 features | ~30 endpoints | Medium |
| **P2** | 6 features | ~22 endpoints | Variable |

**Total new endpoints: ~82** (across all phases)
**Coverage after S3: 112 → ~100%**

## Recommended Start

**Start with P0** — these form the foundation that everything else builds on:
1. **S3-F1** (Accounts) — sub-accounts, hide, reorder, transfer all
2. **S3-F2** (Transaction Filtering) — cursor pagination, list modes, amounts
3. **S3-F3** (Batch Ops) — multi-select, batch update/delete
4. **S3-F4** (Data Management) — stats, export, clear
5. **S3-F5** (Extended Profile) — avatar, language, currency, date format