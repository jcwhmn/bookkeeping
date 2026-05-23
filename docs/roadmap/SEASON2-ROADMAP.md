# Feature Roadmap - Season 2

## Current State (v0.1.0)

| Page | Status | Notes |
|------|--------|-------|
| Login/Register | ✅ Done | Auth with JWT, MD5 password |
| Dashboard | ✅ Basic | 4 stat cards + chart + recent tx |
| Accounts | ✅ CRUD | Create, edit, soft delete, filter by type |
| Categories | ✅ CRUD | Income/expense tabs, create |
| Transactions | ⚠️ Partial | Create only, no edit/delete, no date picker |
| Profile | ⚠️ Basic | Email/nickname only |

---

## Priority 1 — Core Functionality (MVP Polish)

### P1.1 — Transaction Edit & Delete
- [ ] Backend: `PUT /api/v1/transactions/{id}` — update transaction
- [ ] Backend: `DELETE /api/v1/transactions/{id}` — soft delete / remove
- [ ] Backend: Revert account balance on delete
- [ ] Frontend: Click transaction → edit dialog
- [ ] Frontend: Swipe or icon to delete with confirm
- [ ] Reference: OpenAPI `PUT /api/v1/transactions/modify.json`

### P1.2 — Transaction Date Picker
- [ ] Frontend: Date picker in create/edit dialog
- [ ] Backend: Accept `transactionTime` from request (Unix timestamp)
- [ ] Default to now if not provided
- [ ] Reference: OpenAPI `transaction_time` field

### P1.3 — Transfer Support
- [ ] Backend: Create transfer creates TWO transactions (type 4 + type 5)
- [ ] Backend: Set `relatedId` to link the pair
- [ ] Backend: Update both account balances (+source, -dest)
- [ ] Frontend: "Transfer" type button in transaction dialog
- [ ] Frontend: Show "To Account" dropdown when Transfer selected
- [ ] Reference: OpenAPI `related_id` field, TransactionType 4=TRANSFER_OUT, 5=TRANSFER_IN

### P1.4 — Month Navigation (Transactions by Month)
- [ ] Backend: `GET /api/v1/transactions?year=2026&month=5`
- [ ] Backend: `GET /api/v1/transactions/statistics?start_time=&end_time=`
- [ ] Frontend: Month picker in transactions page header
- [ ] Frontend: Show prev/next month arrows
- [ ] Reference: OpenAPI `/api/v1/transactions/list/by_month.json`

---

## Priority 2 — Better UX & Charts

### P2.1 — Enhanced Dashboard Charts
- [ ] Income vs Expense bar chart (by month, last 6 months)
- [ ] Category breakdown pie chart (expenses by category)
- [ ] Balance trend line chart (actual data from transactions)
- [ ] Monthly comparison (this month vs last month)
- [ ] Reference: OpenAPI `/api/v1/transactions/statistics/trends.json`

### P2.2 — Transaction Search & Filter
- [ ] Full-text search on `description` field
- [ ] Filter by date range
- [ ] Filter by amount range (min/max)
- [ ] Sort by amount / date
- [ ] Reference: OpenAPI `keyword`, `amount_min`, `amount_max` params

### P2.3 — Account Reorder (Drag & Drop)
- [ ] Backend: `POST /api/v1/accounts/move.json` — update sortOrder
- [ ] Frontend: Drag handle on account cards
- [ ] Persist order via API

### P2.4 — Category Reorder
- [ ] Backend: `POST /api/v1/categories/reorder` — update sortOrder
- [ ] Frontend: Drag handle in categories list
- [ ] Reference: OpenAPI `sort_order` field

---

## Priority 3 — Tags System

### P3.1 — Tags Backend
- [ ] `Tag` entity (name, color, userId)
- [ ] `TagRepository`
- [ ] `TagService` — CRUD
- [ ] `TagController` — `GET/POST/PUT/DELETE /api/v1/tags`
- [ ] `TransactionTag` join table (transaction_id, tag_id)
- [ ] Reference: OpenAPI `Transaction Tags` section

### P3.2 — Tags Frontend
- [ ] `pages/tags.vue` — list/create/edit/delete tags
- [ ] Add tag chips in transaction create/edit dialog
- [ ] Filter transactions by tag
- [ ] Tag autocomplete when adding to transaction

---

## Priority 4 — Reports & Budgets

### P4.1 — Transaction Statistics Endpoint
- [ ] Backend: `GET /api/v1/transactions/statistics`
- [ ] Returns: total income, total expense, net, by category breakdown
- [ ] Reference: OpenAPI `/api/v1/transactions/statistics.json`

### P4.2 — Budget Feature
- [ ] `Budget` entity (categoryId, amount limit, period: monthly/weekly)
- [ ] `BudgetRepository`, `BudgetService`, `BudgetController`
- [ ] Frontend: `pages/budget.vue` — set budgets per category
- [ ] Dashboard: show budget progress bars
- [ ] Alert when approaching/exceeding budget

### P4.3 — Monthly Reports Page
- [ ] `pages/reports.vue` — summary by month
- [ ] Income vs Expense comparison chart
- [ ] Top spending categories
- [ ] Month-over-month trends

---

## Priority 5 — Data Export & Management

### P5.1 — CSV Export
- [ ] Backend: `GET /api/v1/data/export.csv` — stream CSV download
- [ ] Filters: date range, account, category, type
- [ ] Reference: OpenAPI `/api/v1/data/export.csv`

### P5.2 — Data Statistics
- [ ] Backend: `GET /api/v1/data/statistics.json`
- [ ] Returns: account count, category count, transaction count, tag count
- [ ] Frontend: show stats on profile or settings page

### P5.3 — Clear Data
- [ ] Backend: `POST /api/v1/data/clear/all.json` — clear all user data (with confirmation)
- [ ] Backend: `POST /api/v1/data/clear/transactions.json` — clear transactions only

---

## Priority 6 — Advanced Features

### P6.1 — Transaction Templates
- [ ] `Template` entity (name, transactionType, accountId, categoryId, amount, notes)
- [ ] Frontend: "Save as template" option in transaction dialog
- [ ] Frontend: Quick-add from templates
- [ ] Reference: OpenAPI `Transaction Templates` section

### P6.2 — Account Sub-Accounts (Two-Level Hierarchy)
- [ ] Add `parentId` field to `Account` entity
- [ ] Backend: list with `parentId=null` as top-level, nested children
- [ ] Frontend: expand/collapse parent accounts
- [ ] Reference: OpenAPI `two-level: parent and sub-accounts`

### P6.3 — Reconciliation Statement
- [ ] Backend: `GET /api/v1/transactions/reconciliation_statements?account_id=X`
- [ ] Returns: starting balance, +income, -expense, ending balance, transaction list
- [ ] Frontend: "Reconcile" button on account detail

### P6.4 — User Avatar
- [ ] Backend: `POST /api/v1/users/avatar/update` — multipart upload
- [ ] Frontend: Avatar in header dropdown
- [ ] Reference: OpenAPI `/api/v1/users/avatar/update.json`

### P6.5 — User Settings Page
- [ ] Language selector (en-US, zh-CN)
- [ ] Currency selector (USD, CNY, EUR, etc.)
- [ ] Timezone setting
- [ ] Default account preference
- [ ] Reference: OpenAPI `UserProfileUpdateRequest`

---

## Quick Wins (Same Sprint)

### Q1 — Better Loading States
- [ ] Skeleton loaders on pages
- [ ] Button loading spinners on forms

### Q2 — Toast Notifications
- [ ] Success/error toasts for all actions
- [ ] Use Vuetify `v-snackbar`

### Q3 — Empty States
- [ ] Friendly empty state messages with CTA buttons
- [ ] "No transactions yet" → "Add your first transaction"

### Q4 — Confirm Dialogs
- [ ] Delete account → confirm dialog
- [ ] Delete transaction → confirm dialog
- [ ] Clear all data → confirm with type-to-delete

### Q5 — Currency Formatting
- [ ] Respect user's default currency setting
- [ ] Format amounts with proper thousand separators

---

## Season 2 Scope (v0.2.0)

**Must Have (P1):**
- [ ] Transaction edit/delete with balance revert
- [ ] Transaction date picker
- [ ] Transfer support (two-way transactions)
- [ ] Month navigation for transactions

**Should Have (P2):**
- [ ] Enhanced dashboard charts (real data)
- [ ] Transaction search & filter
- [ ] Account/category reorder

**Nice to Have (P3):**
- [ ] Tags system
- [ ] Transaction statistics endpoint
- [ ] Budget feature
- [ ] CSV export

**Won't Do This Season:**
- 2FA, OAuth, cloud sync, receipt image recognition, MCP server

---

## API Reference (for implementation)

### Transaction Modify
```
PUT /api/v1/transactions/{id}
{
  "transactionType": 3,
  "accountId": 1,
  "categoryId": 5,
  "amount": 8500,
  "transactionTime": 1717104000,
  "description": "Updated lunch"
}
```

### Transaction Delete
```
DELETE /api/v1/transactions/{id}
→ Reverts account balance
```

### Transfer Create
```
POST /api/v1/transactions
{
  "transactionType": 4,        // TRANSFER_OUT
  "accountId": 1,              // Source account
  "destinationAccountId": 2,  // Target account
  "amount": 50000,
  "description": "Transfer to savings"
}
→ Creates two transactions linked by relatedId
```

### Statistics
```
GET /api/v1/transactions/statistics?start_time=1747267200&end_time=1749868800
→ Returns { totalIncome, totalExpense, net, byCategory[] }
```

### Tags
```
GET    /api/v1/tags
POST   /api/v1/tags          { "name": "Groceries", "color": "#FF5722" }
PUT    /api/v1/tags/{id}     { "name": "Food", "color": "#4CAF50" }
DELETE /api/v1/tags/{id}
```