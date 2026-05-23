# Transfer Dialog — Design Spec

**Date**: 2026-05-22
**Source**: Open Design
**Status**: ✅ IMPLEMENTED (backend only)

---

## Wireframe

### Create Transfer Modal

```
┌─────────────────────────────────────────────────────────────┐
│                          (dark overlay)                     │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  Transfer                                      [✕]    │  │
│  ├───────────────────────────────────────────────────────┤  │
│  │                                                       │  │
│  │  ┌──────────────────────────────────────────────┐    │  │
│  │  │ 🔄 Transfer                                   │    │  │
│  │  └──────────────────────────────────────────────┘    │  │
│  │                                                       │  │
│  │  Amount                                               │  │
│  │  ┌─────────────────────────────────────────────┐    │  │
│  │  │ $                                    500.00  │    │  │
│  │  └─────────────────────────────────────────────┘    │  │
│  │                                                       │  │
│  │  From Account          →          To Account         │  │
│  │  ┌──────────────────┐       ┌──────────────────┐     │  │
│  │  │ [💵 Cash        ]│  ──→  │ [🏦 Checking   ]│     │  │
│  │  │    $1,234.56    ]│       │    $8,500.00    ]│     │  │
│  │  └──────────────────┘       └──────────────────┘     │  │
│  │                                                       │  │
│  │  Notes (optional)                                     │  │
│  │  ┌─────────────────────────────────────────────┐    │  │
│  │  │ Moving to savings...                        │    │  │
│  │  └─────────────────────────────────────────────┘    │  │
│  │                                                       │  │
│  ├───────────────────────────────────────────────────────┤  │
│  │              [ Cancel ]    [ 🔄 Save Transfer ]     │  │
│  └───────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

### Transfer Success Modal

```
┌─────────────────────────────────────────────────────┐
│                                                     │
│                   ✅                                │
│              (green checkmark)                     │
│                                                     │
│              Transfer Complete!                    │
│                                                     │
│  ┌───────────────────────────────────────────────┐  │
│  │ 🔄 $500.00 transferred from                 │  │
│  │    Cash to Checking                         │  │
│  └───────────────────────────────────────────────┘  │
│                                                     │
│                 [ Done ]                            │
│                                                     │
└─────────────────────────────────────────────────────┘
```

---

## Design Tokens

### Colors
| Token | Hex | Usage |
|-------|-----|-------|
| `--overlay` | `rgba(0,0,0,0.5)` | Modal backdrop |
| `--primary` | `#1976D2` | Transfer badge, Save button |
| `--success` | `#4CAF50` | Success state checkmark |
| `--bg-surface` | `#FFFFFF` | Modal background |
| `--text-primary` | `#212121` | Labels, values |
| `--text-secondary` | `#757575` | Placeholders, hints |
| `--border` | `#E0E0E0` | Input borders |
| `--error` | `#D32F2F` | Error states |
| `--arrow` | `#1976D2` | Flow indicator |

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

### Animation
| Token | Value | Usage |
|-------|-------|-------|
| `--modal-enter` | scale(0.95) → scale(1) | Modal appear |
| `--overlay-fade` | opacity 0 → 0.5 | Backdrop fade |

---

## Components

### 1. Transfer Badge

| Element | Description |
|---------|-------------|
| Shape | Pill/button |
| Icon | 🔄 transfer icon |
| Text | "Transfer" |
| Color | Blue fill (#1976D2) |
| State | **Locked** — cannot change type |

### 2. Amount Input

| Element | Description |
|---------|-------------|
| Prefix | `$` symbol, non-editable |
| Font | 32px, tabular-nums |
| Format | 2 decimal places on blur |
| Validation | > 0 required |

**States**:
| State | Visual |
|-------|--------|
| Default | Gray border |
| Focus | Blue border, shadow |
| Error | Red border, error message |
| Disabled | Gray background |

### 3. From Account Dropdown

| Element | Description |
|---------|-------------|
| Options | Icon + Name + Balance |
| Label | "From Account" |
| Format | "💵 Cash — $1,234.56" |

### 4. Arrow Indicator

| Element | Description |
|---------|-------------|
| Type | Visual arrow (→) or line |
| Color | Blue (#1976D2) |
| Position | Between From and To dropdowns |

### 5. To Account Dropdown

| Element | Description |
|---------|-------------|
| Options | Icon + Name + Balance |
| Label | "To Account" |
| Filter | **Auto-hides selected From Account** |
| Validation | Required, different from From |

### 6. Notes Field

| Element | Description |
|---------|-------------|
| Type | Textarea |
| Placeholder | "Moving to savings..." |
| Max | 255 characters |
| Required | No |

### 7. Footer Buttons

| Button | Position | Style |
|--------|----------|-------|
| Cancel | Center | Text button |
| Save Transfer | Right | Primary blue with 🔄 icon |

### 8. Success Modal

| Element | Description |
|---------|-------------|
| Icon | ✅ green checkmark |
| Title | "Transfer Complete!" |
| Details | "$500.00 transferred from Cash to Checking" |
| Button | "Done" |

---

## Interactions

### Page Load
1. Modal appears with scale animation
2. Transfer badge pre-selected (blue, locked)
3. Focus on Amount input

### Amount Input
- On focus: Show cursor
- On blur: Format to 2 decimal places
- On keyup: Numbers + decimal only

### From Account Selection
1. User clicks dropdown
2. Shows all accounts with name + balance
3. User selects account
4. Selected account removed from To Account dropdown

### To Account Dropdown
1. Automatically filters out selected From Account
2. Shows remaining accounts
3. If only From Account exists: Show "No other accounts available"
4. User selects different account

### Save Transfer Click
1. Validate all fields
2. If errors: Show error states
3. If valid: Show loading spinner on button
4. Call API: `POST /api/v1/transactions` (type=4)
5. On success: 
   - Close create modal
   - Show success modal with details
6. On "Done": Close success modal, refresh transaction list

### Cancel/Close Click
1. Close modal (no confirmation needed — no state changed)

### Escape Key / Click Outside
1. Close modal

---

## Validation Rules

| Field | Rule | Error Message |
|-------|------|---------------|
| Amount | > 0 | "Amount must be greater than 0" |
| Amount | ≤ Balance | Warning only: "Insufficient balance" |
| From Account | Required | "Please select an account to transfer from" |
| To Account | Required | "Please select an account to transfer to" |
| To Account | Different from From | N/A — auto-filtered |

---

## API Contract

### Create Transfer
```
POST /api/v1/transactions

Request:
{
  "transactionType": 4,           // TRANSFER_OUT
  "accountId": 1,                 // From account (Cash)
  "destinationAccountId": 2,      // To account (Checking)
  "amount": 50000,                 // In cents ($500.00)
  "description": "Moving to savings"
}

Backend Logic:
1. Create TRANSFER_OUT (type=4):
   - accountId=1, amount=-50000, relatedId=<tx2.id>
2. Create TRANSFER_IN (type=5):
   - accountId=2, amount=+50000, relatedId=<tx1.id>
3. Update account 1: balance -= 50000
4. Update account 2: balance += 50000

Response:
{
  "success": true,
  "result": {
    "id": 15,
    "transactionType": 4,
    "accountId": 1,
    "destinationAccountId": 2,
    "amount": 50000,
    "description": "Moving to savings",
    "relatedId": 16,
    "createdAt": 1717104000
  }
}
```

### Balance Update Logic

| Transaction | Account | Amount | Balance Change |
|-------------|---------|--------|----------------|
| TRANSFER_OUT (type=4) | From (Cash) | 50000 | balance -= 50000 |
| TRANSFER_IN (type=5) | To (Checking) | 50000 | balance += 50000 |

---

## Backend Implementation Tasks

### DTO
- [ ] Ensure `CreateTransactionRequest` has `destinationAccountId` field
- [ ] Add validation for transfer: requires destinationAccountId

### Service
- [ ] `TransactionService.createTransaction()` for type=4:
  - [ ] Validate both accounts exist and belong to user
  - [ ] Validate accounts are different
  - [ ] Create TRANSFER_OUT record
  - [ ] Create TRANSFER_IN record with linked relatedId
  - [ ] Update source account balance (decrease)
  - [ ] Update destination account balance (increase)

### Tests
- [ ] Unit test: Transfer creates two records
- [ ] Unit test: Balance updates correct
- [ ] Unit test: Same account validation
- [ ] Integration test: Full transfer flow

---

## Frontend Implementation Tasks

### Components
- [ ] Transfer dialog (similar to transaction dialog but locked type)
- [ ] Success modal component
- [ ] Account dropdown with balance display

### Store
- [ ] Selected accounts state
- [ ] Transfer success state

### API
- [ ] `$fetch` POST call with type=4

### i18n Keys
- [ ] `transfer.title` = "Transfer"
- [ ] `transfer.amount` = "Amount"
- [ ] `transfer.fromAccount` = "From Account"
- [ ] `transfer.toAccount` = "To Account"
- [ ] `transfer.notes` = "Notes"
- [ ] `transfer.save` = "Save Transfer"
- [ ] `transfer.cancel` = "Cancel"
- [ ] `transfer.success` = "Transfer Complete!"
- [ ] `transfer.successMessage` = "{amount} transferred from {from} to {to}"
- [ ] `transfer.done` = "Done"
- [ ] `transfer.sameAccountError` = "Please select a different account"

---

## Edge Cases

| Case | Handling |
|------|----------|
| Only one account | Disable Transfer — need 2+ accounts |
| Same account selected | Auto-hide from To dropdown |
| Insufficient balance | Warning but allow (negative balance possible) |
| Account deleted during edit | Show error, refresh list |
| Transfer to same account | Impossible — filtered out |

---

## States Summary

| State | Visual |
|-------|--------|
| Default | All fields empty, From Account focused |
| Filled | Fields filled, Save enabled |
| Validating | Spinner on Save button |
| Success | Green modal with checkmark |
| Error | Red borders, error messages |

---

## Reuse Strategy

The Transfer dialog shares these with Transaction Edit Dialog:
- Modal overlay styling
- Amount input component
- Account dropdown component
- Notes field
- Footer button layout
- Validation logic

**Differences:**
- Transfer badge locked (no type selector)
- From/To account pair
- No category dropdown
- Success modal variant

---

*Last updated: 2026-05-22*
*Design complete — ready for implementation*