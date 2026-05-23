# Transaction Edit Dialog — Design Spec

**Date**: 2026-05-22
**Source**: Open Design
**File**: `@prototype/transaction-edit-dialog.html`
**Status**: ✅ IMPLEMENTED

---

## Wireframe

### Edit Transaction Modal

```
┌─────────────────────────────────────────────────────────────┐
│                          (dark overlay)                     │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  Edit Transaction                              [✕]    │  │
│  ├───────────────────────────────────────────────────────┤  │
│  │                                                       │  │
│  │  Transaction Type                                     │  │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐              │  │
│  │  │Expense   │ │ Income   │ │Transfer  │              │  │
│  │  │  (−)     │ │  (+)     │ │  (↔)     │              │  │
│  │  └──────────┘ └──────────┘ └──────────┘              │  │
│  │                                                       │  │
│  │  Amount                                               │  │
│  │  ┌─────────────────────────────────────────────┐    │  │
│  │  │ $                                     85.00  │    │  │
│  │  └─────────────────────────────────────────────┘    │  │
│  │                                                       │  │
│  │  From Account                 To Account            │  │
│  │  ┌──────────────────┐   →   ┌──────────────────┐   │  │
│  │  │ [💵 Cash          ]│       │ [🏦 Checking    ]│   │  │
│  │  │    $1,234.56     ]│       │    $8,500.00    ]│   │  │
│  │  └──────────────────┘       └──────────────────┘   │  │
│  │  (only for Transfer type)                          │  │
│  │                                                       │  │
│  │  Category                                            │  │
│  │  ┌─────────────────────────────────────────────┐    │  │
│  │  │ [🍔 Food & Dining                      ▼]  │    │  │
│  │  └─────────────────────────────────────────────┘    │  │
│  │                                                       │  │
│  │  Date                  Time                         │  │
│  │  ┌─────────────────┐   ┌─────────────────┐        │  │
│  │  │ May 15, 2026    │   │ 14:30            │        │  │
│  │  └─────────────────┘   └─────────────────┘        │  │
│  │                                                       │  │
│  │  Notes (optional)                                     │  │
│  │  ┌─────────────────────────────────────────────┐    │  │
│  │  │ Lunch at restaurant                          │    │  │
│  │  └─────────────────────────────────────────────┘    │  │
│  │                                                       │  │
│  ├─────────────────────────────────────────────────────┤  │
│  │ [ Delete ]           [ Cancel ]    [ Save Changes ]  │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Delete Confirmation Dialog

```
┌─────────────────────────────────────────────────────┐
│                                                     │
│                   ⚠️                                │
│              (amber warning icon)                   │
│                                                     │
│              Delete this transaction?               │
│                                                     │
│  ┌───────────────────────────────────────────────┐  │
│  │ 💰 Account balance will revert by $85.00     │  │
│  │ 📁 Category will lose this expense record     │  │
│  └───────────────────────────────────────────────┘  │
│                                                     │
│           [ Keep Transaction ]  [ Delete ]          │
│                 (secondary)      (red)             │
│                                                     │
└─────────────────────────────────────────────────────┘
```

---

## Design Tokens

### Colors
| Token | Hex | Usage |
|-------|-----|-------|
| `--overlay` | `rgba(0,0,0,0.5)` | Modal backdrop |
| `--primary` | `#1976D2` | Save button, active states |
| `--danger` | `#D32F2F` | Delete button |
| `--warning` | `#FF9800` | Warning icon |
| `--bg-surface` | `#FFFFFF` | Modal background |
| `--text-primary` | `#212121` | Labels, values |
| `--text-secondary` | `#757575` | Placeholders, hints |
| `--border` | `#E0E0E0` | Input borders |
| `--error` | `#D32F2F` | Error states |

### Typography
| Token | Value | Usage |
|-------|-------|-------|
| `--font-display` | serif | Modal title |
| `--font-body` | sans-serif | Labels, inputs |
| `--font-mono` | tabular-nums | Amount display |

### Spacing
| Token | Value | Usage |
|-------|-------|-------|
| `--modal-padding` | 24px | Modal inner padding |
| `--field-gap` | 16px | Between form fields |
| `--input-height` | 48px | Input heights |
| `--button-height` | 40px | Footer buttons |

---

## Components

### 1. Transaction Type Selector

| Element | Description |
|---------|-------------|
| Container | 3 pill buttons in a row |
| Buttons | Expense, Income, Transfer |
| Default | No selection in edit (show current type as selected) |
| Active | Filled background with color, white text |
| Colors | Expense: red outline, Income: green outline, Transfer: blue outline |

**States**:
| State | Visual |
|-------|--------|
| Default | Border only, gray text |
| Hover | Light fill |
| Active | Solid fill (#1976D2 for Transfer, matching color), white text |
| Disabled | Gray, no interaction |

### 2. Amount Input

| Element | Description |
|---------|-------------|
| Prefix | `$` symbol, non-editable |
| Font | 32px, tabular-nums for alignment |
| Format | 2 decimal places |
| Max | 999,999,999 (cents: 99999999900) |

**States**:
| State | Visual |
|-------|--------|
| Default | Gray border |
| Focus | Blue border, slight shadow |
| Error | Red border, red helper text below |
| Disabled | Gray background |

### 3. Account Dropdown

| Element | Description |
|---------|-------------|
| Options | Icon + Name + Balance (e.g., "💵 Cash $1,234.56") |
| Selected | Icon + Name shown |

**States**:
| State | Visual |
|-------|--------|
| Default | Gray border |
| Open | Dropdown visible |
| Error | Red border, message below |
| Disabled | Gray background |

### 4. Category Dropdown

| Element | Description |
|---------|-------------|
| Options | Icon + Category Name |
| Filter | Only shows categories matching transaction type |
| Empty | "No categories for this type" message |

### 5. Date Picker

| Element | Description |
|---------|-------------|
| Format | "MMM DD, YYYY" (e.g., "May 15, 2026") |
| Picker | Native date picker on click |
| Validation | Cannot select future dates |

### 6. Time Input

| Element | Description |
|---------|-------------|
| Format | 24-hour "HH:MM" |
| Default | Current time |
| Picker | Native time input |

### 7. Notes Field

| Element | Description |
|---------|-------------|
| Type | Textarea (single line for MVP) |
| Max | 255 characters |
| Required | No (optional) |

### 8. Footer Buttons

| Button | Position | Style |
|--------|----------|-------|
| Delete | Left | Red text, no background |
| Cancel | Center | Text button |
| Save | Right | Primary blue, filled |

### 9. Delete Confirmation Dialog

| Element | Description |
|---------|-------------|
| Icon | ⚠️ in amber circle |
| Title | "Delete this transaction?" |
| Details | Yellow box with balance revert info |
| Keep | Secondary button (gray) |
| Delete | Danger button (red) |

---

## Interactions

### Type Button Click
1. Update active button state
2. Filter category dropdown
3. If Transfer: show "To Account" field with arrow
4. Clear category selection

### Amount Input
- On focus: Show decimal cursor
- On blur: Format to 2 decimal places
- On keyup: Allow only numbers and decimal

### Transfer Mode
1. Show "To Account" dropdown
2. Arrow indicator between From/To
3. Validate: To ≠ From (error if same)

### Save Button Click
1. Validate all fields
2. If errors: Show error states, stop
3. If valid: Show loading spinner
4. Call API: `PUT /api/v1/transactions/{id}`
5. On success: Toast, close modal, refresh list
6. On error: Show error message

### Delete Button Click
1. Open confirmation dialog
2. Show "Are you sure?" with details
3. If "Keep Transaction": Close dialog
4. If "Delete": Show loading, call API
5. Revert account balance
6. On success: Toast, close all, refresh list

### Cancel/Close Click
1. Check for unsaved changes
2. If changed: Show confirmation "Discard changes?"
3. If no changes: Close modal

### Escape Key / Click Outside
1. Same as Cancel (unsaved changes check)

---

## Validation Rules

| Field | Rule | Error Message |
|-------|------|---------------|
| Amount | > 0 | "Amount must be greater than 0" |
| Account | Required | "Please select an account" |
| Category | Required (for Expense/Income) | "Please select a category" |
| To Account | Required + Different (Transfer) | "Please select a different account" |
| Date | Not in future | "Date cannot be in the future" |

---

## API Contract

### Edit Transaction
```
PUT /api/v1/transactions/{id}

Request:
{
  "transactionType": 3,           // 2=Income, 3=Expense, 4=Transfer
  "accountId": 1,                 // From account
  "destinationAccountId": 2,      // Only for Transfer (type 4)
  "categoryId": 5,                 // null for Transfer
  "amount": 8500,                  // In cents (8500 = $85.00)
  "transactionTime": 1716809400,  // Unix timestamp (2024-05-27 14:30)
  "description": "Lunch at restaurant"
}

Response:
{
  "success": true,
  "result": {
    "id": 123,
    "transactionType": 3,
    "accountId": 1,
    "categoryId": 5,
    "amount": 8500,
    "transactionTime": 1716809400,
    "description": "Lunch at restaurant",
    "createdAt": 1716809400,
    "updatedAt": 1716809400
  }
}
```

### Delete Transaction
```
DELETE /api/v1/transactions/{id}

Response:
{
  "success": true,
  "result": null
}
```

---

## Backend Implementation Tasks

### Entity
- [ ] `Transaction` entity already exists — no changes needed

### DTO
- [ ] Create `UpdateTransactionRequest` record
- [ ] Add `transactionTime` to `CreateTransactionRequest`
- [ ] Add `destinationAccountId` to both

### Repository
- [ ] Already exists: `TransactionRepository`

### Service
- [ ] `TransactionService.updateTransaction(id, request)`
  - [ ] Fetch existing transaction
  - [ ] Revert old account balance
  - [ ] Apply new balance if account/amount changed
  - [ ] Update transaction record
  - [ ] Return updated TransactionDto
- [ ] `TransactionService.deleteTransaction(id)`
  - [ ] Fetch transaction
  - [ ] Revert account balance
  - [ ] Delete transaction record

### Controller
- [ ] `PUT /api/v1/transactions/{id}` endpoint
- [ ] `DELETE /api/v1/transactions/{id}` endpoint

### Tests
- [ ] Unit tests for update balance logic
- [ ] Unit tests for delete balance logic
- [ ] Integration tests for both endpoints

---

## Frontend Implementation Tasks

### Components
- [ ] Edit dialog component (reuse create dialog)
- [ ] Delete confirmation dialog
- [ ] Unsaved changes guard

### Store/State
- [ ] Transaction list store
- [ ] Selected transaction for edit

### API
- [ ] `$fetch` PUT call
- [ ] `$fetch` DELETE call

### i18n Keys
- [ ] `transaction.edit` = "Edit Transaction"
- [ ] `transaction.delete` = "Delete"
- [ ] `transaction.deleteConfirm` = "Delete this transaction?"
- [ ] `transaction.deleteRevert` = "Account balance will revert by {amount}"
- [ ] `transaction.saveChanges` = "Save Changes"
- [ ] `transaction.cancel` = "Cancel"
- [ ] `transaction.unsavedChanges` = "You have unsaved changes. Discard?"

---

## Edge Cases

| Case | Handling |
|------|----------|
| Edit deleted account | Error: "Account no longer exists" |
| Edit transfer | Show both From/To accounts, edit notes only |
| Delete transfer | Deletes both TRANSFER_OUT + TRANSFER_IN |
| Amount changed | Revert old balance, apply new balance |
| Account changed | Revert old account, apply new account |
| Category deleted | Show "Category unavailable" |
| No categories for type | Show empty state in dropdown |

---

## States Summary

| State | Visual |
|-------|--------|
| Default (loaded) | All fields pre-filled |
| Editing | Active input highlighted |
| Saving | All fields disabled, spinner on Save |
| Save Error | Error message, fields enabled |
| Delete Confirming | Confirmation dialog open |
| Deleting | All fields disabled, spinner on Delete |
| Delete Success | Toast, close modal |

---

*Last updated: 2026-05-22*
*Design complete — ready for implementation*