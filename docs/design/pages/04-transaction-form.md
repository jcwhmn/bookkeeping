# Page Design - Transaction Form

## Page Purpose
Add or edit transactions (income, expense, or transfer).

## Route
- `/transactions/new`
- `/transactions/:id/edit`

## Transaction Types

| Type | Icon | Description | Effect |
|------|------|-------------|--------|
| Income | 💰 | Money received | +balance |
| Expense | 💸 | Money spent | -balance |
| Transfer | 🔄 | Move between accounts | +/- accounts |

## Layout Structure

### Desktop (Modal Dialog)
```
┌──────────────────────────────────────────────┐
│  [← Back]    Add Transaction           [✕]  │
├──────────────────────────────────────────────┤
│                                              │
│  ┌────────┐ ┌────────┐ ┌────────┐          │
│  │ Expense│ │ Income │ │ Transfer│          │
│  └────────┘ └────────┘ └────────┘          │
│     ●                               ○         │
│                                              │
│  Amount:                                     │
│  ┌──────────────────────────────────────┐   │
│  │ $                        0.00        │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  Account:                                    │
│  ┌──────────────────────────────────────┐   │
│  │ [💵 Cash ▼]                         │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  Category:                                   │
│  ┌──────────────────────────────────────┐   │
│  │ [🍔 Food ▼]                         │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  Date:                                       │
│  ┌──────────────────────────────────────┐   │
│  │ [📅 May 19, 2026 ▼]                 │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  Notes:                                      │
│  ┌──────────────────────────────────────┐   │
│  │ Weekly groceries at Costco           │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  Tags: [food] [groceries] [+]               │
│                                              │
│  ─── Transfer Only ───                      │
│                                              │
│  To Account:                                 │
│  ┌──────────────────────────────────────┐   │
│  │ [🏦 Checking ▼]                     │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  [ Cancel ]        [ Save Transaction ]     │
│                                              │
└──────────────────────────────────────────────┘
```

### Mobile (Full Page)
Same layout but full screen, bottom sticky action bar.

## Components

### Type Selector
- Three large buttons for Expense/Income/Transfer
- Selected type has filled background
- Toggles form fields based on type

### Amount Input
| Feature | Description |
|---------|-------------|
| Currency symbol | Prefix ($ or ¥ or €) |
| Numeric keyboard | Mobile: number pad |
| Thousand separator | Auto-format as user types |
| Cents handling | Auto-append .00 |

### Account Selector
- Dropdown with account icons
- Search/filter for many accounts
- Shows current balance

### Category Selector
- Hierarchical dropdown (parent → child)
- Icon + name display
- Recently used categories at top

### Date Picker
- Calendar widget
- Quick buttons: Today, Yesterday
- Time picker (optional)

### Tags Input
- Chip-style multi-select
- Create new tag inline
- Color-coded chips

### Transfer Fields (conditional)
- "To Account" dropdown
- Shows when Transfer type selected

## Form Validation

| Field | Rules |
|-------|-------|
| Amount | Required, > 0, max 999999999 |
| Account | Required |
| Category | Required (expense/income) |
| Date | Required, not future |
| To Account | Required (transfer), different from Account |

## Data Model

### Create Transaction Request
```javascript
// POST /api/v1/transactions
{
  type: "EXPENSE",                    // EXPENSE | INCOME | TRANSFER
  amount: "8500",                     // String in cents
  accountId: "1",                    // Source account
  destinationAccountId: null,        // For transfers only
  categoryId: "5",                   // For expense/income
  transactionTime: "1717104000",     // Unix timestamp
  notes: "Weekly groceries",
  tags: ["food", "groceries"]         // Create if not exist
}
```

### Success Response
```javascript
{
  "success": true,
  "result": {
    "id": "123",
    "type": "EXPENSE",
    "amount": "8500",
    "accountId": "1",
    "accountBalance": "241500",      // New balance
    "transactionTime": "1717104000",
    "createdAt": "1717105000"
  }
}
```

## States

### Default
- Expense selected
- Amount empty
- Current account selected
- Today's date

### Loading
- Save button shows spinner
- Form disabled

### Success
- Toast notification "Transaction saved"
- Navigate back or clear form for next entry

### Error
- Field-level error messages
- Amount field highlighted if invalid

## Quick Actions

| Action | Description |
|--------|-------------|
| Quick Add | Ctrl/Cmd + N |
| Save & New | After save, reset form |
| Save & Close | After save, close modal |
| Calculate | Split bill equally |

## Design Tokens
- Type buttons: 100px wide, 48px height
- Selected type: primary color bg, white text
- Amount input: 32px font size, right-aligned
- Form field gap: 16px
- Action button: primary color, full width on mobile

## i18n Keys
- `transaction.add` = "Add Transaction"
- `transaction.edit` = "Edit Transaction"
- `transaction.type.expense` = "Expense"
- `transaction.type.income` = "Income"
- `transaction.type.transfer` = "Transfer"
- `transaction.amount` = "Amount"
- `transaction.account` = "Account"
- `transaction.category` = "Category"
- `transaction.toAccount` = "To Account"
- `transaction.date` = "Date"
- `transaction.notes` = "Notes"
- `transaction.tags` = "Tags"
- `transaction.save` = "Save Transaction"
- `transaction.saveNew` = "Save & Add Another"

## OpenDesign Reference
Create transaction form with:
- Type selector (Expense/Income/Transfer)
- Amount input with currency
- Account dropdown
- Category dropdown
- Date picker
- Notes textarea
- Tags input
- Conditional Transfer fields
- Validation states