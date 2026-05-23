# Season 2 Feature Backlog

**Version**: 0.2.0
**Target Date**: ~10 weeks
**Last Updated**: 2026-05-22

---

## How to Read This Document

| Symbol | Meaning |
|--------|---------|
| 🔴 P0 | Must have - blocks MVP |
| 🟡 P1 | Should have - high value |
| 🟢 P2 | Nice to have - polish |
| ✅ Done | Implemented |
| 🔨 In Progress | Being worked on |
| 📋 Todo | Not started |

---

## P0 - Must Have (MVP Polish)

---

### ✅ FEATURE-TXN-001: Transaction Edit (Backend Done)

| Field | Value |
|-------|-------|
| Priority | 🔴 P0 |
| Dependencies | None |
| Status | ✅ Backend Done | Frontend: 📋 Todo |

**User Story**:
> As a user, I want to edit existing transactions so that I can fix mistakes or update details.

**Acceptance Criteria**:
- [ ] Click transaction row → opens edit dialog
- [ ] Dialog pre-filled with current values (type, amount, account, category, notes)
- [ ] Save updates transaction and reverts old account balance, applies new balance
- [ ] Cancel discards changes
- [ ] Error if trying to edit deleted account

**API Contract**:
```
PUT /api/v1/transactions/{id}
Request:
{
  "transactionType": 2|3|4,
  "accountId": 1,
  "categoryId": 5,
  "amount": 8500,
  "transactionTime": 1717104000,
  "description": "Updated lunch"
}

Response:
{
  "success": true,
  "result": { TransactionDto }
}
```

**Backend Tasks**:
- [x] `TransactionService.updateTransaction(id, request)`
- [x] Revert old account balance (if account changed or amount changed)
- [x] Apply new account balance
- [x] Unit tests

**Frontend Tasks**:
- [ ] Click handler on transaction list row
- [ ] Edit dialog (reuse create dialog, toggle title)
- [ ] Pre-fill form with transaction data
- [ ] Save button → PUT request

---

### ✅ FEATURE-TXN-002: Transaction Delete (Backend Done)

| Field | Value |
|-------|-------|
| Priority | 🔴 P0 |
| Dependencies | None |
| Status | ✅ Backend Done | Frontend: 📋 Todo |

**User Story**:
> As a user, I want to delete transactions so that I can remove errors or unwanted entries.

**Acceptance Criteria**:
- [ ] Delete icon appears on transaction row hover
- [ ] Click shows confirmation dialog ("Delete this transaction?")
- [ ] Confirm → transaction removed, account balance reverted
- [ ] Cancel → no change
- [ ] Cannot undo (soft delete not needed for transactions)

**API Contract**:
```
DELETE /api/v1/transactions/{id}

Response:
{
  "success": true,
  "result": null
}
```

**Backend Tasks**:
- [x] `TransactionController.deleteTransaction(id)`
- [x] `TransactionService.deleteTransaction(id)`
- [x] Revert account balance by amount
- [x] Delete from DB
- [x] Unit tests

**Frontend Tasks**:
- [ ] Delete icon button on transaction row
- [ ] Confirmation dialog (v-dialog)
- [ ] DELETE request on confirm
- [ ] Remove from list on success

---

### ✅ FEATURE-TXN-003: Transaction Date Picker (Backend Done)

| Field | Value |
|-------|-------|
| Priority | 🔴 P0 |
| Dependencies | None |
| Status | ✅ Backend Done | Frontend: 📋 Todo |

**User Story**:
> As a user, I want to set transaction date/time so that I can record past expenses.

**Acceptance Criteria**:
- [ ] Date picker appears in create/edit transaction dialog
- [ ] Default: current date/time
- [ ] Can select past dates (up to 1 year ago)
- [ ] Cannot select future dates
- [ ] Time picker (optional, default to current time)
- [ ] Shows in transaction list and detail

**API Contract**:
```
Request body includes:
  "transactionTime": 1717104000  // Unix timestamp in seconds

Response includes:
  "transactionTime": 1717104000
```

**Backend Tasks**:
- [x] Accept `transactionTime` in `CreateTransactionRequest`
- [x] Accept `transactionTime` in `UpdateTransactionRequest`
- [x] Default to current time if not provided
- [ ] Validate not in future
- [x] Unit tests

**Frontend Tasks**:
- [ ] Add Vuetify date picker (v-date-picker or v-menu + text field)
- [ ] Add time input (v-text-field with HH:MM format)
- [ ] Convert to Unix timestamp before API call
- [ ] Display formatted date in list

---

### ✅ FEATURE-TXN-004: Transfer Support (Backend Done)

| Field | Value |
|-------|-------|
| Priority | 🔴 P0 |
| Dependencies | None |
| Status | ✅ Backend Done | Frontend: 📋 Todo |

**User Story**:
> As a user, I want to transfer money between accounts so that I can move funds without manual double-entry.

**Acceptance Criteria**:
- [ ] "Transfer" button in transaction type selector
- [ ] "To Account" dropdown appears when Transfer selected
- [ ] Cannot select same account for both source and destination
- [ ] Creates TWO linked transactions (type 4 + type 5)
- [ ] Both linked by `relatedId`
- [ ] Source account: balance decreases
- [ ] Destination account: balance increases
- [ ] Transfer shows in transaction list with → indicator

**API Contract**:
```
POST /api/v1/transactions
Request:
{
  "transactionType": 4,           // TRANSFER_OUT
  "accountId": 1,                 // Source account
  "destinationAccountId": 2,    // Target account
  "amount": 50000,
  "description": "Transfer to savings"
}

Response:
{
  "success": true,
  "result": { TransactionDto with relatedId linking to pair }
}

Creates:
- TX A: type=4, accountId=source, relatedId=B
- TX B: type=5, accountId=dest, relatedId=A
```

**Backend Tasks**:
- [x] Add `destinationAccountId` to `CreateTransactionRequest`
- [x] `TransactionService.createTransaction()` handles type 4
- [x] Auto-create TRANSFER_IN (type 5) with linked `relatedId`
- [x] Update source account: -amount
- [x] Update destination account: +amount
- [x] Unit tests for both directions

**Frontend Tasks**:
- [ ] Add "Transfer" to type selector (Income/Expense/Transfer)
- [ ] Show "To Account" select when Transfer selected
- [ ] Validate: source != destination
- [ ] Display transfer indicator in list (→ icon)
- [ ] Show destination account name in subtitle

---

### 📋 FEATURE-TXN-005: Month Navigation

| Field | Value |
|-------|-------|
| Priority | 🔴 P0 |
| Estimated Effort | 3 days |
| Dependencies | FEATURE-TXN-003 (Date Picker) |
| Status | 📋 Todo |

**User Story**:
> As a user, I want to browse transactions by month so that I can review historical data.

**Acceptance Criteria**:
- [ ] Month picker in transactions page header
- [ ] Shows current month by default
- [ ] Prev/Next arrows to navigate months
- [ ] Shows transactions only for selected month
- [ ] URL updates with month parameter (optional)
- [ ] Summary cards update with filtered data

**API Contract**:
```
GET /api/v1/transactions?year=2026&month=5
GET /api/v1/transactions?year=2026&month=4

Response:
{
  "success": true,
  "result": [ TransactionDto ... ]
}
```

**Backend Tasks**:
- [ ] Add `year` and `month` query params to `TransactionController.recent()`
- [ ] `TransactionRepository.findByUserIdAndYearAndMonth()`
- [ ] Calculate start/end timestamps for month
- [ ] Return only transactions in that month

**Frontend Tasks**:
- [ ] Add month selector in page header (v-btn with icon)
- [ ] Click opens v-menu with month/year picker
- [ ] Prev/Next v-btn icons
- [ ] Fetch with month params
- [ ] Update URL with `?month=2026-05`

---

## P1 - Should Have

---

### 📋 FEATURE-CHART-001: Enhanced Dashboard Charts

| Field | Value |
|-------|-------|
| Priority | 🟡 P1 |
| Estimated Effort | 4 days |
| Dependencies | FEATURE-STATS-001 |
| Status | 📋 Todo |

**User Story**:
> As a user, I want to see visual charts of my finances so that I can understand spending patterns.

**Acceptance Criteria**:
- [ ] Income vs Expense bar chart (last 6 months)
- [ ] Expense breakdown pie chart (by category)
- [ ] Balance trend line chart (actual calculated balance over time)
- [ ] Charts update when filters change
- [ ] Tooltips show exact values
- [ ] Responsive (works on mobile)

**API Contract**:
```
GET /api/v1/transactions/statistics/trends.json?start_year_month=202601&end_year_month=202606

Response:
{
  "success": true,
  "result": {
    "trends": [
      { "yearMonth": "202601", "income": 5000000, "expense": 3000000 },
      ...
    ]
  }
}
```

**Backend Tasks** (see FEATURE-STATS-001):
- [ ] `GET /api/v1/transactions/statistics`
- [ ] `GET /api/v1/transactions/statistics/trends`
- [ ] Group by month, sum by type

**Frontend Tasks**:
- [ ] Replace mock chart with real data
- [ ] Income/Expense bar chart (ECharts)
- [ ] Category pie chart (ECharts)
- [ ] Balance line chart (calculated from transactions)

---

### 📋 FEATURE-SEARCH-001: Transaction Search & Filter

| Field | Value |
|-------|-------|
| Priority | 🟡 P1 |
| Estimated Effort | 3 days |
| Dependencies | None |
| Status | 📋 Todo |

**User Story**:
> As a user, I want to search transactions so that I can find specific entries quickly.

**Acceptance Criteria**:
- [ ] Keyword search on description
- [ ] Date range filter (from/to)
- [ ] Amount range filter (min/max)
- [ ] Sort by: date (default), amount
- [ ] Clear all filters button
- [ ] Filter count badge when active

**API Contract**:
```
GET /api/v1/transactions?keyword=lunch&min_amount=1000&max_amount=50000&sort=amount
GET /api/v1/transactions?start_time=1747267200&end_time=1749868800
```

**Backend Tasks**:
- [ ] Add `keyword`, `minAmount`, `maxAmount`, `sort` params
- [ ] `TransactionRepository` with LIKE query for keyword
- [ ] `BETWEEN` query for amount range
- [ ] ORDER BY sort field

**Frontend Tasks**:
- [ ] Expand filter bar
- [ ] Date range inputs
- [ ] Amount range inputs
- [ ] Sort dropdown
- [ ] Clear filters button

---

### 📋 FEATURE-REORDER-001: Account & Category Reorder

| Field | Value |
|-------|-------|
| Priority | 🟡 P1 |
| Estimated Effort | 3 days |
| Dependencies | None |
| Status | 📋 Todo |

**User Story**:
> As a user, I want to reorder accounts and categories so that I can organize them my way.

**Acceptance Criteria**:
- [ ] Drag handle on account/category cards
- [ ] Drag to reorder
- [ ] Visual feedback during drag
- [ ] Persist order via API
- [ ] Order saved per user

**API Contract**:
```
POST /api/v1/accounts/move.json
Request:
{
  "order": [3, 1, 5, 2, 4]  // IDs in new order
}

POST /api/v1/categories/reorder
Request:
{
  "order": [1, 3, 5, 2, 4, 6, 7, 8, 9, 10, 11, 12]
}
```

**Backend Tasks**:
- [ ] Add `sortOrder` field to `Account` and `Category` entities
- [ ] V4__add_sort_order.sql migration
- [ ] `AccountController.reorder(order[])`
- [ ] `CategoryController.reorder(order[])`
- [ ] Update sortOrder for all items

**Frontend Tasks**:
- [ ] Add vuedraggable or native drag support
- [ ] Update API on drop
- [ ] Re-render list on success

---

## P2 - Nice to Have

---

### 📋 FEATURE-TAGS-001: Tags System

| Field | Value |
|-------|-------|
| Priority | 🟢 P2 |
| Estimated Effort | 5 days |
| Dependencies | None |
| Status | 📋 Todo |

**User Story**:
> As a user, I want to tag transactions so that I can categorize spending beyond categories.

**Acceptance Criteria**:
- [ ] Tags page (list/create/edit/delete)
- [ ] Color picker for each tag
- [ ] Add tags to transactions (multi-select chips)
- [ ] Filter transactions by tag
- [ ] Tag autocomplete when adding to transaction

**DB Schema**:
```sql
CREATE TABLE tags (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  name VARCHAR(64) NOT NULL,
  color VARCHAR(7) NOT NULL,  -- hex color e.g. "#FF5722"
  sort_order INT DEFAULT 0,
  created_at BIGINT NOT NULL,
  updated_at BIGINT NOT NULL,
  UNIQUE(user_id, name)
);

CREATE TABLE transaction_tags (
  transaction_id BIGINT NOT NULL,
  tag_id BIGINT NOT NULL,
  PRIMARY KEY (transaction_id, tag_id),
  FOREIGN KEY (transaction_id) REFERENCES transactions(id),
  FOREIGN KEY (tag_id) REFERENCES tags(id)
);
```

**API Contract**:
```
GET    /api/v1/tags
POST   /api/v1/tags          { "name": "Groceries", "color": "#FF5722" }
PUT    /api/v1/tags/{id}     { "name": "Food", "color": "#4CAF50" }
DELETE /api/v1/tags/{id}

// Transaction with tags:
GET /api/v1/transactions?with_tags=true
→ Includes "tags": [{"id": 1, "name": "Food", "color": "#FF5722"}]
```

---

### 📋 FEATURE-STATS-001: Transaction Statistics

| Field | Value |
|-------|-------|
| Priority | 🟢 P2 |
| Estimated Effort | 4 days |
| Dependencies | FEATURE-CHART-001 |
| Status | 📋 Todo |

**User Story**:
> As a user, I want to see spending statistics so that I can understand my financial patterns.

**Acceptance Criteria**:
- [ ] Total income for period
- [ ] Total expense for period
- [ ] Net (income - expense)
- [ ] Breakdown by category (top 10)
- [ ] Month-over-month trends (last 6 months)

**API Contract**:
```
GET /api/v1/transactions/statistics?start_time=1747267200&end_time=1749868800

Response:
{
  "success": true,
  "result": {
    "totalIncome": 9000000,
    "totalExpense": 4500000,
    "net": 4500000,
    "byCategory": [
      { "categoryId": 5, "categoryName": "Food", "income": 0, "expense": 250000 },
      ...
    ]
  }
}
```

---

### 📋 FEATURE-BUDGET-001: Budgets

| Field | Value |
|-------|-------|
| Priority | 🟢 P2 |
| Estimated Effort | 5 days |
| Dependencies | FEATURE-STATS-001 |
| Status | 📋 Todo |

**User Story**:
> As a user, I want to set budgets so that I can control my spending.

**Acceptance Criteria**:
- [ ] Set monthly budget per category
- [ ] Progress bar on dashboard
- [ ] Alert when approaching (80%) or exceeding budget
- [ ] View all budgets on dedicated page

**DB Schema**:
```sql
CREATE TABLE budgets (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  category_id BIGINT NOT NULL,
  amount_limit BIGINT NOT NULL,  -- in cents
  period VARCHAR(10) DEFAULT 'monthly',  -- monthly|weekly
  created_at BIGINT NOT NULL,
  updated_at BIGINT NOT NULL,
  UNIQUE(user_id, category_id)
);
```

---

### 📋 FEATURE-REPORTS-001: Monthly Reports Page

| Field | Value |
|-------|-------|
| Priority | 🟢 P2 |
| Estimated Effort | 4 days |
| Dependencies | FEATURE-STATS-001 |
| Status | 📋 Todo |

**User Story**:
> As a user, I want to see monthly reports so that I can review my finances.

**Acceptance Criteria**:
- [ ] Month selector
- [ ] Income vs Expense comparison
- [ ] Top spending categories
- [ ] Biggest transactions
- [ ] Month-over-month trend

---

### 📋 FEATURE-EXPORT-001: CSV Export

| Field | Value |
|-------|-------|
| Priority | 🟢 P2 |
| Estimated Effort | 3 days |
| Dependencies | None |
| Status | 📋 Todo |

**User Story**:
> As a user, I want to export my data so that I can analyze it in Excel.

**Acceptance Criteria**:
- [ ] Export button on transactions page
- [ ] Filter before export (date range, accounts, categories)
- [ ] Downloads CSV file
- [ ] Includes all transaction fields

**API Contract**:
```
GET /api/v1/data/export.csv?start_time=1747267200&end_time=1749868800

Response:
Content-Type: text/csv
Content-Disposition: attachment; filename="transactions_2026-05.csv"

Date,Type,Account,Category,Amount,Notes
2026-05-15,Expense,Cash,Food,8500,Weekly groceries
...
```

---

### 📋 FEATURE-SETTINGS-001: User Settings Page

| Field | Value |
|-------|-------|
| Priority | 🟢 P2 |
| Estimated Effort | 3 days |
| Dependencies | None |
| Status | 📋 Todo |

**User Story**:
> As a user, I want to configure my preferences so that the app suits my needs.

**Acceptance Criteria**:
- [ ] Language selector (English, 中文)
- [ ] Currency selector (USD, CNY, EUR, etc.)
- [ ] Default account preference
- [ ] Avatar upload
- [ ] Change password

---

## Quick Wins (Same Sprint)

### 📋 FEATURE-UX-001: Loading States + Toasts
| Priority | 🟢 P2 | Status | 📋 Todo |

- [ ] Skeleton loaders on all pages
- [ ] Button spinners during API calls
- [ ] Success/error toasts (v-snackbar)
- [ ] Empty state messages with CTA

### 📋 FEATURE-UX-002: Confirm Dialogs
| Priority | 🟢 P2 | Status | 📋 Todo |

- [ ] Delete account → confirm
- [ ] Delete transaction → confirm
- [ ] Clear all data → type-to-confirm

---

## Dependency Graph

```
FEATURE-STATS-001 (Transaction Statistics)
    ↑
FEATURE-CHART-001 (Enhanced Charts) ← needs stats first
    ↑
FEATURE-BUDGET-001 (Budgets) ← needs stats
    ↑
FEATURE-REPORTS-001 (Monthly Reports) ← needs stats

FEATURE-TXN-001 (Edit Transaction)
FEATURE-TXN-002 (Delete Transaction) ← can start immediately
FEATURE-TXN-003 (Date Picker) ← can start immediately
FEATURE-TXN-004 (Transfer) ← can start immediately
FEATURE-TXN-005 (Month Navigation) ← needs FEATURE-TXN-003

FEATURE-TAGS-001 (Tags) ← can start immediately
FEATURE-SEARCH-001 (Search) ← can start immediately
FEATURE-REORDER-001 (Reorder) ← can start immediately
FEATURE-EXPORT-001 (CSV Export) ← can start immediately
FEATURE-SETTINGS-001 (Settings) ← can start immediately

FEATURE-UX-001 (Loading/Toasts) ← can start immediately
FEATURE-UX-002 (Confirm Dialogs) ← can start immediately
```

---

## Sprint Planning

| Sprint | Duration | Features | Goal |
|--------|----------|----------|------|
| Sprint 1 | 2 weeks | TXN-001, TXN-002, TXN-003 | Transaction CRUD |
| Sprint 2 | 2 weeks | TXN-004, TXN-005, SEARCH-001 | Transfer + Navigation |
| Sprint 3 | 2 weeks | TAGS-001, STATS-001, CHART-001 | Tags + Charts |
| Sprint 4 | 2 weeks | BUDGET-001, REPORTS-001, REORDER-001 | Reports + Polish |
| Sprint 5 | 2 weeks | EXPORT-001, SETTINGS-001, UX-001, UX-002 | Final Polish |

---

*Last updated: 2026-05-22*
*14 features, 4 quick wins, ~10 weeks*