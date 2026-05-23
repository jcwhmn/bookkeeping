# CSV Export Dialog — Design Spec

**Date**: 2026-05-22
**Source**: Open Design
**Status**: ✅ Designed

---

## Wireframe

### Export Dialog

```
┌───────────────────────────────────────────────────────────────────────────────┐
│                          (dark overlay)                                       │
│  ┌─────────────────────────────────────────────────────────────────────────┐  │
│  │  Export Transactions                                    [✕]              │  │
│  ├─────────────────────────────────────────────────────────────────────────┤  │
│  │                                                                           │  │
│  │  Date Range                                                               │  │
│  │                                                                           │  │
│  │  Quick:  ● This Month   ○ Last Month   ○ All Time   ○ Custom            │  │
│  │                                                                           │  │
│  │  ┌─────────────────┐           ┌─────────────────┐                      │  │
│  │  │ From: May 1    📅]        │ To: May 22    📅]│                      │  │
│  │  └─────────────────┘           └─────────────────┘                      │  │
│  │                                                                           │  │
│  │  Account                                                                   │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │
│  │  │ All Accounts (4)                                         [▼]       │   │  │
│  │  └─────────────────────────────────────────────────────────────────────┘   │  │
│  │                                                                           │  │
│  │  Category                                                                  │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │
│  │  │ All Categories (11)                                    [▼]       │   │  │
│  │  └─────────────────────────────────────────────────────────────────────┘   │  │
│  │                                                                           │  │
│  │  Include Options                                                          │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │
│  │  │ ☑ Include transfers                                               │   │  │
│  │  │   ☑ Include transfer pair                                         │   │  │
│  │  └─────────────────────────────────────────────────────────────────────┘   │  │
│  │                                                                           │  │
│  │  Preview                                                                  │  │
│  │  ┌─────────────────────────────────────────────────────────────────────┐   │  │
│  │  │                                                                       │   │  │
│  │  │  342 transactions will be exported                                 │   │  │
│  │  │  May 1, 2026 → May 22, 2026                                        │   │  │
│  │  │  Estimated size: ~45 KB                                            │   │  │
│  │  │                                                                       │   │  │
│  │  └─────────────────────────────────────────────────────────────────────┘   │  │
│  │                                                                           │  │
│  │  File: transactions2026-05-01to_2026-05-22.csv                           │  │
│  │                                                                           │  │
│  ├─────────────────────────────────────────────────────────────────────────┤  │
│  │              [ Cancel ]    [ 📥 Download CSV ]                        │  │
│  │               (text)         (primary blue)                           │  │
│  └─────────────────────────────────────────────────────────────────────────┘  │
└───────────────────────────────────────────────────────────────────────────────┘
```

### Quick Options Expanded (This Month selected)

```
│  Quick:  ● This Month   ○ Last Month   ○ All Time   ○ Custom            │  │
│                     ✓                                                                  │
│  ┌─────────────────┐           ┌─────────────────┐                      │
│  │ From: May 1    📅]        │ To: May 22    📅]│                      │
│  └─────────────────┘           └─────────────────┘                      │
```

### Multi-Select Dropdown Open (Accounts)

```
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │ All Accounts (4)                                         [▲]       │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  ☑ All Accounts                                                       │   │
│  │  ───────────────────────────────────────────────────────────────────  │   │
│  │  ☑ 💵 Cash               $1,234.56                                   │   │
│  │  ☑ 🏦 Chase Checking     $2,847.50                                   │   │
│  │  ☐ 💳 Visa Credit Card  -$432.10                                    │   │
│  │  ☐ 🏠 Savings          $8,500.00                                     │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
```

### Success State

```
┌───────────────────────────────────────────────────────────────────────────────┐
│                          (dark overlay)                                       │
│  ┌─────────────────────────────────────────────────────────────────────────┐  │
│  │  Export Transactions                                    [✕]              │  │
│  ├─────────────────────────────────────────────────────────────────────────┤  │
│  │                                                                           │  │
│  │                                                                           │  │
│  │                                                                           │  │
│  │                            ✅                                            │  │
│  │                    (green checkmark)                                   │  │
│  │                                                                           │  │
│  │                   Export Complete!                                      │  │
│  │                                                                           │  │
│  │            transactions2026-05-01to_2026-05-22.csv                     │  │
│  │                     has been downloaded                                  │  │
│  │                                                                           │  │
│  │                                                                           │  │
│  ├─────────────────────────────────────────────────────────────────────────┤  │
│  │                            [ Done ]                                     │  │
│  └─────────────────────────────────────────────────────────────────────────┘  │
└───────────────────────────────────────────────────────────────────────────────┘
```

---

## Design Tokens

### Colors
| Token | Hex | Usage |
|-------|-----|-------|
| `--primary` | `#1976D2` | Download button, links |
| `--success` | `#4CAF50` | Success checkmark |
| `--bg-surface` | `#FFFFFF` | Dialog background |
| `--bg-page` | `#FAFAFA` | Page background |
| `--bg-dropdown` | `#FFFFFF` | Dropdown background |
| `--bg-selected` | `#F5F5F5` | Selected item background |
| `--text-primary` | `#212121` | Labels, values |
| `--text-secondary` | `#757575` | Hints, counts |
| `--border` | `#E0E0E0` | Borders, dividers |
| `--checkbox` | `#1976D2` | Checked checkbox |

### Typography
| Token | Value | Usage |
|-------|-------|-------|
| `--font-display` | serif | Dialog title |
| `--font-body` | sans-serif | Body text |
| `--font-mono` | monospace | Filename, amounts |

### Spacing
| Token | Value | Usage |
|-------|-------|-------|
| `--dialog-padding` | 24px | Dialog padding |
| `--field-gap` | 16px | Between form fields |
| `--checkbox-gap` | 8px | Checkbox spacing |
| `--button-height` | 40px | Action buttons |

---

## Components

### 1. Dialog Title

| Element | Description |
|---------|-------------|
| Text | "Export Transactions" (serif) |
| Close | ✕ button right side |

### 2. Quick Date Options

| Element | Description |
|---------|-------------|
| Type | Radio buttons |
| Options | This Month, Last Month, All Time, Custom |
| Default | This Month selected |

**States**:
| State | Visual |
|-------|--------|
| Selected | Filled circle, blue |
| Unselected | Empty circle, gray |
| Custom | Shows date pickers |

### 3. Date Pickers

| Element | Description |
|---------|-------------|
| Label | "From:" and "To:" |
| Default From | First day of current month |
| Default To | Today |
| Format | "MMM D" display |

**States**:
| State | Visual |
|-------|--------|
| Default | Gray border |
| Focus | Blue border |
| Invalid | Red border, error message |

### 4. Multi-Select Dropdown

| Element | Description |
|---------|-------------|
| Button | Shows selection summary |
| Dropdown | Checkbox list |
| "All" Option | First item, selects/deselects all |
| Item | Checkbox + label |

**Button Format**:
| Selection | Button Text |
|-----------|-------------|
| All | "All Accounts (4)" |
| Some | "3 selected" |
| None | "Select..." |

**Item Format**:
| Item | Format |
|------|--------|
| Account | "💵 Cash — $1,234.56" |
| Category | "🍔 Food & Dining" |

### 5. Checkbox Options

| Element | Description |
|---------|-------------|
| Include Transfers | Top-level checkbox |
| Include Transfer Pair | Indented checkbox, depends on above |
| Style | Standard checkboxes |

**Logic**:
| Include Transfers | Include Transfer Pair | State |
|------------------|------------------------|-------|
| Unchecked | Disabled | Both unchecked |
| Checked | Can check/uncheck | Normal |

### 6. Preview Section

| Element | Description |
|---------|-------------|
| Background | Light gray (#F5F5F5) |
| Count | "N transactions will be exported" |
| Range | Date range display |
| Size | "Estimated size: ~X KB" |

### 7. File Name Preview

| Element | Description |
|---------|-------------|
| Font | Monospace |
| Format | "transactions{date_from}to_{date_to}.csv" |
| Updates | Real-time with date changes |

### 8. Footer Buttons

| Button | Position | Style |
|--------|----------|-------|
| Cancel | Center | Text button |
| Download | Right | Primary blue with 📥 icon |

### 9. Success State

| Element | Description |
|---------|-------------|
| Icon | ✅ green checkmark (large) |
| Title | "Export Complete!" |
| Filename | Monospace, filename |
| Subtitle | "has been downloaded" |
| Button | "Done" (centered) |

---

## Interactions

### Page Load
1. Set default dates (start of month, today)
2. Select "This Month" radio
3. Select "All Accounts"
4. Select "All Categories"
5. Check both include options
6. Calculate preview count

### Quick Option Click
1. Clear any custom date selection
2. Set From/To based on option:
   - This Month: May 1 → Today
   - Last Month: April 1 → April 30
   - All Time: First transaction → Today
   - Custom: Enable date pickers
3. Update preview

### Date Picker Change
1. Update From or To date
2. Switch to "Custom" radio
3. Update preview
4. Update filename

### Account Multi-Select
1. Click dropdown
2. Show checkbox list
3. Click item to toggle
4. Click "All Accounts" to select/deselect all
5. Update button text
6. Update preview

### Category Multi-Select
1. Click dropdown
2. Show checkbox list
3. Click item to toggle
4. Click "All Categories" to select/deselect all
5. Update button text
6. Update preview

### Include Transfers Toggle
1. Toggle checkbox
2. If unchecked: Disable "Include transfer pair"
3. Update preview

### Preview Update
1. Calculate based on all filters
2. Show transaction count
3. Show date range
4. Estimate file size (rough: 200 bytes per transaction)

### Download CSV Click
1. Show loading on button
2. Call API: GET /api/v1/data/export.csv?params...
3. On success:
   - Show success state
   - Trigger file download
4. On error:
   - Show error message
   - Keep dialog open

### Success Done Click
1. Close dialog
2. Reset state

### Cancel/Close Click
1. Close dialog
2. Reset state

### Escape / Click Outside
1. Close dialog
2. Reset state

---

## Validation Rules

| Rule | Error Message |
|------|---------------|
| From ≤ To | "End date must be after start date" |
| Date range | Max 1 year or configurable |
| At least one filter | Allow all as default |

---

## API Contract

### Export CSV
```
GET /api/v1/data/export.csv?start_date=2026-05-01&end_date=2026-05-22&account_ids=1,2&category_ids=5,6,7&include_transfers=true&include_transfer_pair=true

Headers:
Content-Type: text/csv
Content-Disposition: attachment; filename="transactions2026-05-01to_2026-05-22.csv"

Response: (CSV file)
Date,Type,Account,Category,Amount,Notes,Tags
2026-05-15,Expense,Cash,Food & Dining,-8500,Weekly groceries,
2026-05-14,Expense,Chase,Transportation,-4500,Gas station,
2026-05-10,Transfer,Checking,Savings,-50000,Moving to savings,Transfer
...
```

### Preview Count
```
GET /api/v1/data/export/preview?start_date=...&end_date=...&account_ids=...&category_ids=...

Response:
{
  "success": true,
  "result": {
    "transactionCount": 342,
    "estimatedSize": 45000,
    "dateRange": { "from": "2026-05-01", "to": "2026-05-22" }
  }
}
```

---

## CSV Format

### Columns
| Column | Format | Example |
|--------|--------|---------|
| Date | YYYY-MM-DD | 2026-05-15 |
| Type | Name | Expense, Income, Transfer |
| Account | Name | Cash, Chase Checking |
| Category | Name | Food & Dining |
| Amount | Cents (with sign) | -8500, +50000 |
| Amount Display | Formatted | -$85.00, +$500.00 |
| Notes | Text | Weekly groceries |
| Tags | Comma-separated | Food, Groceries |

### Transfer Format
When "Include transfer pair" is checked, transfers show both sides:
```
Date,Type,Account,Category,Amount,Notes,Related
2026-05-15,Transfer Out,Cash,Savings,-50000,Moving to savings,→ Checking
2026-05-15,Transfer In,Checking,Savings,+50000,Moving to savings,← Cash
```

---

## Backend Implementation Tasks

### Controller
- [ ] `GET /api/v1/data/export.csv` — download endpoint
- [ ] `GET /api/v1/data/export/preview` — count endpoint

### Service
- [ ] `ExportService.buildCsv(params)` — generate CSV
- [ ] `ExportService.getTransactionQuery(params)` — filter query
- [ ] `ExportService.estimateSize(count)` — size estimate

### Repository
- [ ] Extend existing query methods with export filters

### CSV Generation
- [ ] Use StringBuilder or Apache Commons CSV
- [ ] Proper escaping for special characters
- [ ] UTF-8 BOM for Excel compatibility

### Tests
- [ ] Unit tests for CSV generation
- [ ] Integration test for full export

---

## Frontend Implementation Tasks

### Components
- [ ] `ExportDialog.vue` — main export dialog
- [ ] `QuickDateOptions.vue` — radio options
- [ ] `DateRangePicker.vue` — from/to dates
- [ ] `MultiSelectDropdown.vue` — reusable multi-select
- [ ] `IncludeOptions.vue` — checkboxes
- [ ] `ExportPreview.vue` — preview section
- [ ] `FileNamePreview.vue` — filename display
- [ ] `ExportSuccess.vue` — success state

### Composables
- [ ] `useExport()` — export state and logic
- [ ] `useDateRange()` — date range state
- [ ] `usePreview()` — preview calculation

### API
- [ ] GET /api/v1/data/export.csv (with query params)
- [ ] GET /api/v1/data/export/preview

### i18n Keys
- [ ] `export.title` = "Export Transactions"
- [ ] `export.dateRange` = "Date Range"
- [ ] `export.quick` = "Quick"
- [ ] `export.thisMonth` = "This Month"
- [ ] `export.lastMonth` = "Last Month"
- [ ] `export.allTime` = "All Time"
- [ ] `export.custom` = "Custom"
- [ ] `export.from` = "From"
- [ ] `export.to` = "To"
- [ ] `export.account` = "Account"
- [ ] `export.category` = "Category"
- [ ] `export.allAccounts` = "All Accounts"
- [ ] `export.allCategories` = "All Categories"
- [ ] `export.includeTransfers` = "Include transfers"
- [ ] `export.includePair` = "Include transfer pair"
- [ ] `export.preview` = "Preview"
- [ ] `export.willExport` = "{count} transactions will be exported"
- [ ] `export.estimatedSize` = "Estimated size: ~{size}"
- [ ] `export.file` = "File"
- [ ] `export.cancel` = "Cancel"
- [ ] `export.download` = "Download CSV"
- [ ] `export.success` = "Export Complete!"
- [ ] `export.downloaded` = "has been downloaded"
- [ ] `export.done` = "Done"
- [ ] `export.selected` = "{count} selected"

---

## Edge Cases

| Case | Handling |
|------|----------|
| No transactions | Show "0 transactions will be exported" |
| Very large export | Show warning for >10,000 records |
| Export in progress | Disable button, show spinner |
| Export fails | Show error, keep dialog open |
| Special characters | Escape properly in CSV |

---

## States Summary

| State | Visual |
|-------|--------|
| Default | Form filled with defaults |
| Customizing | User changing options |
| Preview Loading | Brief loading on preview |
| Downloading | Spinner on Download button |
| Success | Checkmark + success message |
| Error | Error message in dialog |

---

*Last updated: 2026-05-22*
*Design complete — ready for implementation*