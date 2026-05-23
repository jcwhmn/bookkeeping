# Open Design Guide - Bookkeeping Season 2

**Date**: 2026-05-22
**Purpose**: Step-by-step guide to design Season 2 pages using Open Design

---

## How to Open Open Design

```bash
# Method 1: Direct executable
"C:\Users\Administrator\AppData\Local\Programs\Open Design release-stable-win\Open Design.exe"

# Method 2: Create a shortcut
# Right-click desktop → New → Shortcut
# Path: "C:\Users\Administrator\AppData\Local\Programs\Open Design release-stable-win\Open Design.exe"
```

Or visit: https://open-design.ai/

---

## Design Workflow

### Step 1: Open Open Design

### Step 2: Select Skill
Use the `dashboard` skill for this project (transaction management + finance app)

### Step 3: Enter Prompt
Type a detailed prompt describing the page you want to design

### Step 4: Review Output
Open Design will generate an HTML mockup with:
- Visual wireframe/layout
- Design tokens (colors, fonts, spacing)
- Component structure

### Step 5: Copy to Docs
Save the design output to `docs/design/pages/XX-page-name.md`

---

## Prompts for Season 2 Pages

### Page 1: Transaction Edit/Delete Dialog
```
Design a bookkeeping transaction edit dialog with:
- Modal overlay on dark background
- Transaction type selector: Expense / Income / Transfer (3 buttons)
- Amount input field with currency symbol
- Account dropdown selector
- Category dropdown (filtered by type)
- Date picker with time input
- Notes text field
- Delete button (red, left side)
- Cancel button (text, middle)
- Save button (primary color, right side)
- Delete confirmation dialog with warning message
```

### Page 2: Transfer Transaction Dialog
```
Design a bookkeeping transfer transaction dialog with:
- Modal overlay
- "Transfer" type pre-selected
- Amount input with currency symbol
- "From Account" dropdown (shows name + balance)
- "To Account" dropdown (different from From)
- Notes field
- Save Transfer button
- Visual indicator showing money flow: Account A → Account B
```

### Page 3: Month Navigation Component
```
Design a bookkeeping month navigation header with:
- Left arrow button (<)
- Month/Year display (e.g., "May 2026")
- Right arrow button (>)
- Click to open month picker dropdown
- Current month highlighted
- Quick jump to current month button
```

### Page 4: Transaction Search/Filter Bar
```
Design a bookkeeping transaction filter bar with:
- Keyword search input with icon
- Date range: From date + To date pickers
- Account dropdown filter
- Category dropdown filter
- Amount range: Min + Max inputs
- Sort dropdown: Date (default), Amount, Category
- Clear All Filters button
- Active filter count badge
```

### Page 5: Tags Management Page
```
Design a bookkeeping tags management page with:
- Page title: "Tags" with "+ Add Tag" button
- Search/filter input
- Tag list: color dot + name + color code + edit/delete icons
- Create/Edit tag dialog: name input + color picker with presets
- Delete confirmation with transaction count warning
- Tag usage stats section at bottom
```

### Page 6: Budget Dashboard
```
Design a bookkeeping budget dashboard with:
- Page title: "Budgets"
- Month selector
- Budget cards per category:
  - Category name + icon
  - Progress bar (spent vs limit)
  - Amount: $X of $Y spent
  - Status indicator: OK (green), Warning 80%+ (yellow), Over (red)
- Total budget summary card
- Add/Edit budget dialog
```

### Page 7: Monthly Reports Page
```
Design a bookkeeping monthly reports page with:
- Month/Year selector at top
- Summary cards: Total Income, Total Expense, Net
- Income vs Expense comparison bar chart
- Top spending categories breakdown
- Biggest transactions list (top 10)
- Month-over-month trend mini chart
- Export to CSV button
```

### Page 8: User Settings Page
```
Design a bookkeeping user settings page with:
- Profile section: avatar, name, email
- Language selector dropdown (English, 中文)
- Currency selector dropdown (USD, CNY, EUR)
- Default account preference
- Change password section
- Notification preferences
- Danger zone: Delete account button
```

### Page 9: Transaction Statistics Page
```
Design a bookkeeping statistics page with:
- Date range selector (This Month, Last Month, Last 6 Months, Custom)
- Summary metrics: Total Income, Total Expense, Net, Transaction Count
- Income/Expense trend line chart (6 months)
- Expense breakdown pie chart by category
- Top 10 transactions table
- Category-wise breakdown table
```

### Page 10: CSV Export Dialog
```
Design a bookkeeping CSV export dialog with:
- Export title: "Export Transactions"
- Date range selector: From + To dates
- Account filter (multi-select or "All Accounts")
- Category filter (multi-select or "All Categories")
- Include transferred transactions toggle
- Preview: "X transactions will be exported"
- Export button (downloads CSV)
- Filename preview: "transactions_2026-05.csv"
```

---

## After Design: Implementation Checklist

For each designed page:

- [ ] **Backend API**
  - [ ] Entity updated/created
  - [ ] Repository methods
  - [ ] Service layer logic
  - [ ] Controller endpoints
  - [ ] Unit tests

- [ ] **Frontend**
  - [ ] API composable updated
  - [ ] Page component created
  - [ ] Dialogs/Forms implemented
  - [ ] i18n keys added
  - [ ] Styles matching design

- [ ] **QA**
  - [ ] Manual test cases written
  - [ ] Test execution signed off
  - [ ] Edge cases covered

---

## Design Tokens Reference

When Open Design outputs design tokens, use these for implementation:

| Token | Value | Usage |
|-------|-------|-------|
| `--color-primary` | #1976D2 | Buttons, links, active states |
| `--color-secondary` | #424242 | Secondary actions |
| `--color-error` | #D32F2F | Delete buttons, errors |
| `--color-success` | #388E3C | Success states |
| `--color-warning` | #F57C00 | Warnings |
| `--font-display` | serif | Page titles |
| `--font-body` | sans-serif | Body text |
| `--spacing-sm` | 8px | Compact spacing |
| `--spacing-md` | 16px | Default spacing |
| `--spacing-lg` | 24px | Section spacing |

---

## Example Design Output Format

Save Open Design output as Markdown with this structure:

```markdown
# Page Name

## Wireframe
[ASCII or description of layout]

## Design Tokens
- Primary: #1976D2
- Accent: #FF5722
- ...

## Components
1. **Component Name**
   - States: default, hover, active, disabled
   - Interactions: click → action

## API Contract
- GET /api/v1/resource
- POST /api/v1/resource
- PUT /api/v1/resource/{id}
- DELETE /api/v1/resource/{id}

## Edge Cases
- Empty state: ...
- Error state: ...
```

---

*Last updated: 2026-05-22*
*All Season 2 pages should be designed via Open Design first*