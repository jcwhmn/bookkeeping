---
name: bookkeeping-design
description: "Use Open Design to design pages for the Bookkeeping application. Applies to bookkeeping-specific UI design: transaction forms, accounts pages, category editors, dashboard, reports, budgets, tags, CSV export, and all Season 2+ pages. Always use this skill when the user asks to design, mockup, or wireframe a bookkeeping page."
---

# Bookkeeping Design

Use [Open Design](https://open-design.ai/) to generate UI mockups for Bookkeeping pages. Open Design is an AI design agent that produces interactive HTML prototypes using skills.

## Workflow

1. **Open Open Design Desktop** → https://open-design.ai/

2. **Select the skill** based on page type:

   | Page Type | Skill | Best for |
   |-----------|-------|----------|
   | Dashboard / Analytics | `dashboard` | KPI cards, charts, sidebar nav |
   | Data List / Table | `dashboard` | Transaction lists, accounts, categories |
   | Form / Dialog | `dashboard` | Add/edit transaction, create account |
   | Transfer Flow | `dashboard` | Multi-account transfer |
   | Settings / Profile | `dashboard` | User settings |
   | Mobile Screen | `mobile-app` | Mobile bookkeeping |
   | General Landing Page | `web-prototype` | Landing pages, marketing |
   | Rough Exploration | `wireframe-sketch` | Early-stage sketching |

3. **Provide the brief** — describe the page with specific fields and requirements. Be concrete: list every field, dropdown, button, and data column needed.

4. **Get the design** — Open Design generates an HTML mockup + design tokens.

5. **Save the output** — Save Open Design's output to `docs/design/pages/` as a `.md` file (include the HTML artifact and design tokens).

6. **Implement** — Backend API first (controller, service, repository), then frontend matching the design.

## Design Specs Location

```
docs/design/pages/
├── 01-login.md              # v0.1 ✓
├── 02-dashboard.md          # v0.1 ✓
├── 03-accounts.md           # v0.1 ✓
├── 04-transaction-form.md
├── 05-transactions-list.md
├── 06-categories.md
├── 07-transactions-edit.md
├── 08-transactions-transfer.md
├── 09-tags.md
├── 10-month-navigation.md
├── 11-transactions-search.md
├── 12-tags-management.md
├── 13-budgets.md
├── 14-reports.md
├── 15-settings.md
├── 16-statistics.md
└── 17-csv-export.md
```

## Page Design Briefs

When requesting a design from Open Design, use these ready-made briefs:

### Transaction List Page
```
Design a bookkeeping transactions list page with:
- Fixed sidebar navigation (Dashboard, Accounts, Categories, Transactions, Tags, Budgets, Reports, Statistics, Profile)
- Transaction list table: date, description, amount (color-coded), account, category, tags
- Filter bar: date range picker, account dropdown, category dropdown, tag filter, search input
- Month/year picker in header (prev/next arrows + month selector)
- "Add Transaction" button (opens form dialog)
- Batch actions: delete selected
- Inline edit on row click (opens edit dialog)
- Pagination at bottom
```

### Transaction Form (Add/Edit Dialog)
```
Design a bookkeeping transaction add/edit dialog with:
- Form fields: date picker, description, amount (input with +/- sign selector), account dropdown, category dropdown, tags multi-select
- Transaction type selector: Income / Expense / Transfer (tabs or radio)
- For Transfer: source account + destination account dropdowns
- Save / Cancel buttons
- Edit mode: pre-fill fields, show delete button
- Validation: required fields highlighted
- Amount field shows currency symbol prefix
```

### Dashboard
```
Design a bookkeeping dashboard with:
- Sidebar: Dashboard, Accounts, Categories, Transactions, Tags, Budgets, Reports, Statistics, Profile
- Top: Month/year title + prev/next arrows
- KPI row: Total Income, Total Expense, Net Balance, Budget Remaining
- Charts: Expense breakdown (pie/donut), Monthly trend (line), Top categories (bar)
- Recent transactions table (5 rows)
- Quick actions: Add Income, Add Expense, Transfer
```

### Budgets Page
```
Design a bookkeeping budgets page with:
- Sidebar nav
- Month/year picker
- Budget cards: category name, budget amount, spent amount, progress bar (color: green/yellow/red based on %), remaining
- Add Budget button
- Edit/delete budget inline
- Summary row: Total budgeted, Total spent, Total remaining
```

### Reports Page
```
Design a bookkeeping reports page with:
- Sidebar nav
- Month/year selector
- Report types tabs: Monthly Summary / Category Breakdown / Income vs Expense / Budget vs Actual
- Each report type shows a summary table and chart
- Export button (CSV)
- Date range filter
```

### Tags Management
```
Design a bookkeeping tags management page with:
- Sidebar nav
- Tags list: tag name, color dot, usage count, created date
- Add Tag button (opens dialog)
- Edit tag: rename, change color
- Delete tag (confirm dialog)
- Search/filter tags
- Tag cloud view toggle
```

### CSV Export
```
Design a bookkeeping CSV export feature with:
- Export dialog triggered from transactions list
- Options: date range, account filter, category filter, include tags toggle, include transfers toggle
- Preview: show first 5 rows of what will be exported
- Column selector checkboxes
- Download button → downloads CSV file
- Filename preview: bookkeeping_transactions_2025-01.csv
```

## Design Tokens to Extract

After receiving an Open Design mockup, extract and document these tokens:

```markdown
## Design Tokens

### Colors
| Token | Value | Use |
|-------|-------|-----|
| `--primary` | #1A73E8 | Buttons, active states |
| `--accent` | #34A853 | Income, positive amounts |
| `--danger` | #EA4335 | Expense, delete, negative amounts |

### Typography
| Token | Value |
|-------|-------|
| Font family | Inter, system-ui |
| Heading | 24px bold |
| Body | 14px regular |
| Caption | 12px muted |

### Spacing
| Token | Value |
|-------|-------|
| Sidebar width | 240px |
| Card padding | 16px |
| Grid gap | 16px |
```

## Implementation Order

1. **Backend API first** — define the API contract before writing UI code
2. **Frontend match** — implement the Vue component to match the design
3. **Connect** — wire up API calls to the UI
4. **Test** — verify the UI renders correctly and interactions work