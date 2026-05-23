# Budget Dashboard — Design Spec

**Date**: 2026-05-22
**Source**: Open Design
**Status**: ✅ Designed

---

## Wireframe

### Budget Dashboard (With Data)

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                                                                  │
│   Budgets                           [◀]  May 2026  [▶]                           │
│                                                                                  │
│  ┌───────────────────────────────────────────────────────────────────────────┐  │
│  │ ⚠️ 2 categories over budget                                               │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                                                                  │
│  ┌───────────────────────────────────────────────────────────────────────────┐  │
│  │                                                                           │  │
│  │  Total Budget    Total Spent       Remaining                              │  │
│  │     $2,850       $2,042             $808                                  │  │
│  │   (blue gradient background)         (green text)                        │  │
│  │                                                                           │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                                                                  │
│  ┌───────────────────────────────────────────────────────────────────────────┐  │
│  │                                                                           │  │
│  │  🍔 Food & Dining                     🟢 On track                        │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │
│  │  │ ████████████████████████████████████░░░░░░░░░░░░░░░░░░░░░░░░░░░░░ │   │  │
│  │  └─────────────────────────────────────────────────────────────────────┘   │  │
│  │  $325 of $500 spent                                                       │  │
│  │                                                                           │  │
│  ├───────────────────────────────────────────────────────────────────────────┤  │
│  │                                                                           │  │
│  │  🚗 Transportation                    🟡 Almost there                   │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │
│  │  │ ██████████████████████████████████████████████████████████████████░░ │   │  │
│  │  └─────────────────────────────────────────────────────────────────────┘   │  │
│  │  $480 of $500 spent                                                       │  │
│  │                                                                           │  │
│  ├───────────────────────────────────────────────────────────────────────────┤  │
│  │                                                                           │  │
│  │  🛍️ Shopping                         🔴 Over budget                    │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │
│  │  │ ████████████████████████████████████████████████████████████████░░░░ │   │  │
│  │  └─────────────────────────────────────────────────────────────────────┘   │  │
│  │  $750 of $500 spent              (red border, light red background)        │  │
│  │                                                                           │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                                                                  │
│  [+ Set Budget]                                                                 │
│                                                                                  │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Over-Budget Alert Banner

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                                                                  │
│  ⚠️  2 categories over budget                                                   │
│                                                                                  │
│  ┌───────────────────────────────────────────────────────────────────────────┐  │
│  │                                                                           │  │
│  │  • Shopping: $750 of $500 budget                                         │  │
│  │  • Entertainment: $180 of $150 budget                                    │  │
│  │                                                                           │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                                                                  │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Add/Edit Budget Dialog

```
┌───────────────────────────────────────────────────────────────────────────────┐
│                          (dark overlay)                                       │
│  ┌─────────────────────────────────────────────────────────────────────────┐  │
│  │  Set Budget                                            [✕]              │  │
│  ├─────────────────────────────────────────────────────────────────────────┤  │
│  │                                                                           │  │
│  │  Category                                                                   │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │
│  │  │ [Select category...                                         ▼]       │   │  │
│  │  └─────────────────────────────────────────────────────────────────────┘   │  │
│  │  (Dropdown shows only categories without existing budget)                  │  │
│  │                                                                           │  │
│  │  Monthly Limit                                                             │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │
│  │  │ $                                                            500    │   │  │
│  │  └─────────────────────────────────────────────────────────────────────┘   │  │
│  │                                                                           │  │
│  │  Preview                                                                   │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │
│  │  │ 🍔 Food & Dining: $500/month                                       │   │  │
│  │  └─────────────────────────────────────────────────────────────────────┘   │  │
│  │                                                                           │  │
│  ├─────────────────────────────────────────────────────────────────────────┤  │
│  │              [ Cancel ]                           [ Save ]                │  │
│  └─────────────────────────────────────────────────────────────────────────┘  │
└───────────────────────────────────────────────────────────────────────────────┘
```

### Delete Confirmation Dialog

```
┌───────────────────────────────────────────────────────────────────────────────┐
│                          (dark overlay)                                       │
│  ┌─────────────────────────────────────────────────────────────────────────┐  │
│  │  Delete Budget?                                        [✕]              │  │
│  ├─────────────────────────────────────────────────────────────────────────┤  │
│  │                                                                           │  │
│  │                          🔴                                              │  │
│  │                     (red warning icon)                                  │  │
│  │                                                                           │  │
│  │               Remove budget for "Shopping"?                              │  │
│  │                                                                           │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │
│  │  │ ⚠️ You will no longer track spending for this category.            │   │  │
│  │  └─────────────────────────────────────────────────────────────────────┘   │  │
│  │                                                                           │  │
│  ├─────────────────────────────────────────────────────────────────────────┤  │
│  │              [ Keep Budget ]                        [ Delete ]            │  │
│  │               (secondary)                         (red button)          │  │
│  └─────────────────────────────────────────────────────────────────────────┘  │
└───────────────────────────────────────────────────────────────────────────────┘
```

### Empty State

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                                                                  │
│   Budgets                           [◀]  May 2026  [▶]                           │
│                                                                                  │
│  ┌───────────────────────────────────────────────────────────────────────────┐  │
│  │                                                                           │  │
│  │     [illustration placeholder]                                           │  │
│  │                                                                           │  │
│  │       Set up your first budget                                            │  │
│  │                                                                           │  │
│  │    Track spending by category and get alerts when you're                  │  │
│  │    approaching or exceeding your limits.                                 │  │
│  │                                                                           │  │
│  │    [ Set Budget ]                                                        │  │
│  │                                                                           │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                                                                  │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## Design Tokens

### Colors
| Token | Hex | Usage |
|-------|-----|-------|
| `--primary` | `#1976D2` | Summary card gradient start |
| `--primary-dark` | `#1565C0` | Summary card gradient end |
| `--success` | `#4CAF50` | Under 80% |
| `--warning` | `#FF9800` | 80-99% spent |
| `--danger` | `#D32F2F` | Over budget |
| `--danger-light` | `#FFEBEE` | Over-budget card bg |
| `--warning-bg` | `#FFF3E0` | Warning box bg |
| `--bg-surface` | `#FFFFFF` | Card background |
| `--text-primary` | `#212121` | Main text |
| `--text-secondary` | `#757575` | Labels |
| `--border` | `#E0E0E0` | Borders |

### Status Colors
| Status | Percentage | Color | Label |
|--------|------------|-------|-------|
| 🟢 On track | 0-79% | `#4CAF50` | "On track" |
| 🟡 Almost there | 80-99% | `#FF9800` | "Almost there" |
| 🔴 Over budget | 100%+ | `#D32F2F` | "Over budget" |

### Typography
| Token | Value | Usage |
|-------|-------|-------|
| `--font-display` | serif | Page title |
| `--font-body` | sans-serif | Body text |
| `--font-mono` | tabular-nums | Amounts |

### Spacing
| Token | Value | Usage |
|-------|-------|-------|
| `--card-padding` | 20px | Budget card padding |
| `--progress-height` | 12px | Progress bar height |
| `--field-gap` | 16px | Between form fields |
| `--dialog-padding` | 24px | Dialog padding |

---

## Components

### 1. Page Header

| Element | Description |
|---------|-------------|
| Title | "Budgets" (serif) |
| Month Nav | ◀ Month Year ▶ (same as transaction page) |

### 2. Over-Budget Alert Banner

| Element | Description |
|---------|-------------|
| Type | Warning banner, orange/amber |
| Visibility | Only when categories over budget |
| Content | "N categories over budget" + list |
| Click | Scrolls to over-budget cards |

**States**:
| State | Visual |
|-------|--------|
| Hidden | No categories over budget |
| Visible | Orange banner with list |

### 3. Summary Card

| Element | Description |
|---------|-------------|
| Background | Blue gradient |
| Layout | 3 columns: Total Budget, Total Spent, Remaining |
| Numbers | Large, white, tabular-nums |
| Remaining | Green color if positive, red if negative |

### 4. Budget Card

| Element | Description |
|---------|-------------|
| Category | Emoji icon + name |
| Status | Badge with color + label |
| Progress | Bar with fill % |
| Amount | "$X of $Y spent" |

**States**:
| State | Visual |
|-------|--------|
| Normal | White background |
| Warning | Light yellow tint (80-99%) |
| Over budget | Light red background, red border |
| Hover | Slight elevation, cursor pointer |

### 5. Progress Bar

| Element | Description |
|---------|-------------|
| Track | Gray background (#E0E0E0) |
| Fill | Color based on percentage |
| Height | 12px |
| Border | Rounded (6px radius) |

**Fill Color Logic**:
| Percentage | Color |
|------------|-------|
| 0-79% | Green (#4CAF50) |
| 80-99% | Yellow (#FF9800) |
| 100%+ | Red (#D32F2F) |

### 6. Status Badge

| Element | Description |
|---------|-------------|
| Shape | Pill with colored dot + text |
| Dot | 8px circle |
| Text | Status label |

### 7. Category Dropdown

| Element | Description |
|---------|-------------|
| Filter | Only shows categories without budget |
| Format | Icon + category name |
| Disabled | Categories with existing budget |

### 8. Amount Input

| Element | Description |
|---------|-------------|
| Prefix | "$" symbol |
| Font | Tabular-nums |
| Format | 2 decimal places on blur |

### 9. Preview

| Element | Description |
|---------|-------------|
| Content | Category + amount preview |
| Updates | Real-time as user fills form |

### 10. Delete Confirmation

| Element | Description |
|---------|-------------|
| Icon | 🔴 red circle with warning |
| Title | "Delete Budget?" |
| Details | Category name + warning |
| Keep | Secondary button |
| Delete | Red button |

---

## Interactions

### Page Load
1. Fetch budgets for selected month
2. Fetch transactions for selected month
3. Calculate spent amounts per category
4. Render budget cards with progress
5. Check for over-budget categories
6. Show alert banner if needed

### Month Navigation
1. User clicks ◀ or ▶
2. Update selected month
3. Refetch budgets/transactions for new month
4. Update progress bars

### Budget Card Click
1. Open Edit dialog with current budget
2. Pre-fill category and amount
3. Focus on amount input

### Add Budget Click
1. Open Create dialog
2. Show only categories without budget
3. Focus on category dropdown

### Save Budget Click
1. Validate category + amount
2. If error: Show message
3. If valid: Show loading
4. Call API: POST or PUT
5. On success: Close, refresh, show toast
6. On error: Show error

### Delete Budget Click
1. Open confirmation
2. Show category name
3. If "Keep Budget": Close dialog
4. If "Delete": Show loading
5. Call API: DELETE
6. On success: Close, refresh, toast

### Cancel/Close
1. Close dialog
2. Discard changes

### Escape / Click Outside
1. Close dialog

---

## Validation Rules

| Rule | Error Message |
|------|---------------|
| Category required | "Please select a category" |
| Amount > 0 | "Monthly limit must be greater than 0" |
| Amount ≤ 999,999,999 | "Amount is too large" |
| Unique category | "A budget for this category already exists" |

---

## API Contract

### Get Budgets for Month
```
GET /api/v1/budgets?year=2026&month=5

Response:
{
  "success": true,
  "result": [
    { 
      "id": 1,
      "categoryId": 5,
      "categoryName": "Food & Dining",
      "categoryIcon": "🍔",
      "monthlyLimit": 50000,
      "spent": 32500,
      "period": "monthly"
    }
  ]
}
```

### Create Budget
```
POST /api/v1/budgets

Request:
{
  "categoryId": 5,
  "monthlyLimit": 50000
}

Response:
{
  "success": true,
  "result": {
    "id": 1,
    "categoryId": 5,
    "monthlyLimit": 50000,
    "period": "monthly"
  }
}
```

### Update Budget
```
PUT /api/v1/budgets/{id}

Request:
{
  "monthlyLimit": 60000
}

Response:
{
  "success": true,
  "result": { ... }
}
```

### Delete Budget
```
DELETE /api/v1/budgets/{id}

Response:
{ "success": true, "result": null }
```

### Get Categories for Budget
```
GET /api/v1/categories?without_budget=true

Response:
{
  "success": true,
  "result": [
    { "id": 6, "name": "Entertainment", "icon": "🎬" },
    { "id": 7, "name": "Health", "icon": "💊" }
  ]
}
```

---

## Database Schema

```sql
CREATE TABLE budgets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    category_id BIGINT NOT NULL REFERENCES categories(id),
    amount_limit BIGINT NOT NULL,  -- in cents
    period VARCHAR(10) DEFAULT 'monthly',
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    UNIQUE(user_id, category_id)
);
```

---

## Backend Implementation Tasks

### Entity
- [ ] `Budget` entity class

### Migration
- [ ] `V4__add_budgets.sql` — create budgets table

### Repository
- [ ] `BudgetRepository`
- [ ] `TransactionRepository.sumByCategoryAndMonth()`

### Service
- [ ] `BudgetService` — CRUD
- [ ] `BudgetService.getBudgetsWithSpent(month)` — calculate spent
- [ ] `BudgetService.getCategoriesWithoutBudget()` — for dropdown

### Controller
- [ ] `GET /api/v1/budgets?year&month`
- [ ] `POST /api/v1/budgets`
- [ ] `PUT /api/v1/budgets/{id}`
- [ ] `DELETE /api/v1/budgets/{id}`
- [ ] `GET /api/v1/categories?without_budget=true`

### DTO
- [ ] `BudgetDto`
- [ ] `BudgetWithSpentDto` — includes calculated spent
- [ ] `CreateBudgetRequest`
- [ ] `UpdateBudgetRequest`

### Tests
- [ ] Unit tests for spent calculation
- [ ] Integration tests

---

## Frontend Implementation Tasks

### Pages
- [ ] `budgets.vue` — main budgets page

### Components
- [ ] `BudgetAlertBanner.vue` — over-budget warning
- [ ] `BudgetSummaryCard.vue` — totals display
- [ ] `BudgetCard.vue` — category budget card
- [ ] `BudgetProgressBar.vue` — progress visualization
- [ ] `BudgetDialog.vue` — add/edit dialog
- [ ] `BudgetDeleteDialog.vue` — delete confirmation
- [ ] `MonthNavigation.vue` — reuse from transactions

### Composables
- [ ] `useBudgets()` — budget state and calculations
- [ ] `useMonthNavigation()` — reuse

### Store
- [ ] `useBudgetsStore()` — budgets state

### API
- [ ] GET /api/v1/budgets?year&month
- [ ] POST /api/v1/budgets
- [ ] PUT /api/v1/budgets/{id}
- [ ] DELETE /api/v1/budgets/{id}
- [ ] GET /api/v1/categories?without_budget=true

### i18n Keys
- [ ] `budgets.title` = "Budgets"
- [ ] `budgets.setBudget` = "Set Budget"
- [ ] `budgets.editBudget` = "Edit Budget"
- [ ] `budgets.deleteBudget` = "Delete Budget"
- [ ] `budgets.totalBudget` = "Total Budget"
- [ ] `budgets.totalSpent` = "Total Spent"
- [ ] `budgets.remaining` = "Remaining"
- [ ] `budgets.category` = "Category"
- [ ] `budgets.monthlyLimit` = "Monthly Limit"
- [ ] `budgets.spent` = "{spent} of {limit} spent"
- [ ] `budgets.onTrack` = "On track"
- [ ] `budgets.almostThere` = "Almost there"
- [ ] `budgets.overBudget` = "Over budget"
- [ ] `budgets.overBudgetAlert` = "{count} categories over budget"
- [ ] `budgets.emptyTitle` = "Set up your first budget"
- [ ] `budgets.emptyDescription` = "Track spending by category and get alerts..."
- [ ] `budgets.keep` = "Keep Budget"
- [ ] `budgets.save` = "Save"
- [ ] `budgets.cancel` = "Cancel"

---

## Edge Cases

| Case | Handling |
|------|----------|
| No budgets set | Show empty state |
| All categories budgeted | Hide "Set Budget" button |
| Over 100% | Progress bar fills full, turns red |
| Month with no transactions | Spent = 0, show "On track" |
| Delete budget | Just removes budget, doesn't delete transactions |
| Negative remaining | Show in red, "Over by $X" |

---

## States Summary

| State | Visual |
|-------|--------|
| Loading | Skeleton loaders |
| Empty | Empty state with illustration |
| With Budgets | Budget cards list |
| Over Budget | Alert banner + red card borders |
| Dialog Open | Modal overlay |
| Saving | Spinner on Save |
| Deleting | Spinner on Delete |

---

*Last updated: 2026-05-22*
*Design complete — ready for implementation*