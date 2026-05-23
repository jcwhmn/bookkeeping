# Monthly Reports Page — Design Spec

**Date**: 2026-05-22
**Source**: Open Design
**Status**: ✅ Designed

---

## Wireframe

### Reports Page Overview

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                                                                  │
│   Reports                               [◀]  May 2026  [▶]    [Export ▼]       │
│                                                                                  │
│  ┌───────────────────────────────────────────────────────────────────────────┐  │
│  │  Total Income ↑          Total Expenses ↓      Net Balance     Txns   │  │
│  │      $8,450                $5,820              +$2,630        47     │  │
│  │     (green)                (red)               (blue)                │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                                                                  │
│  ┌───────────────────────────────────────────────────────────────────────────┐  │
│  │                                                                       │  │
│  │  Income vs Expenses (Last 6 Months)                                 │  │
│  │                                                                       │  │
│  │            Jan    Feb    Mar    Apr    May    Jun                     │  │
│  │         ┌────┐                                                        │  │
│  │  Income │    │ ┌────┐                                ┌────┐         │  │
│  │         │ $5 │ │ $6 │ ┌────┐                        │ $8 │            │  │
│  │         │ ,0 │ │ ,2 │ │ $7 │ ┌────┐  ┌────┐  ┌────┐ │ ,4 │            │  │
│  │         │ 00 │ │ 00 │ │ $6 │ │ $7 │  │ $7 │  │ $8 │ │ 50 │            │  │
│  │         │ 00 │ │ 00 │ │ 50 │ │ 80 │  │ 20 │  │ 45 │ │ 00 │            │  │
│  │         │ 00 │ │ 00 │ │ 00 │ │ 00 │  │ 00 │  │ 00 │ │ 00 │            │  │
│  │         └──┬──┘ └──┬──┘ └──┬──┘ └──┬──┘ └──┬──┘ └──┬──┘ └──┬──┘         │  │
│  │              │      │      │      │      │      │                       │  │
│  │         ┌────┴─┐┌──┴───┐┌──┴──┐┌──┴──┐┌──┴──┐┌──┴──┐                    │  │
│  │  Expense │ $3 │ │ $4  │ │ $4 │ │ $5 │ │ $5 │ │ $5 │                    │  │
│  │         │  200│ │  500│ │  200│ │  100│ │  820│ │  950│                    │  │
│  │         │  00 │ │  00 │ │  00 │ │  00 │ │  00 │ │  00 │                    │  │
│  │         └─────┘ └─────┘ └─────┘ └─────┘ └─────┘ └─────┘                    │  │
│  │                                                                       │  │
│  │  Legend: [■ Green Income] [■ Red Expenses]                            │  │
│  │                                                                       │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                                                                  │
│  ┌───────────────────────────────────────────────────────────────────────────┐  │
│  │                                                                       │  │
│  │  Expense Breakdown                           Legend                    │  │
│  │  ┌───────────────────────────┐            ┌────────────────────────┐  │  │
│  │  │                           │            │                        │  │  │
│  │  │       Food                │            │ ■ Food      $1,250  21%│  │  │
│  │  │       21%                │            │ ■ Transport $890   15% │  │  │
│  │  │                         │            │ ■ Shopping  $780   13% │  │  │
│  │  │    Transport             │            │ ■ Other     $700   12% │  │  │
│  │  │      15%          Shop   │            │ ■ Bills     $600   10% │  │  │
│  │  │                  13%    │            │ ■ Entertain $520    9% │  │  │
│  │  │                         │            │ ■ Health    $480    8% │  │  │
│  │  │             $5,820      │            │ ■ Bills     $600   10% │  │  │
│  │  │              Total      │            │ (scrollable list)     │  │  │
│  │  └───────────────────────────┘            └────────────────────────┘  │  │
│  │                                                                       │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                                                                  │
│  ┌───────────────────────────────────────────────────────────────────────────┐  │
│  │  Top 10 Transactions                                                   │  │
│  │  ┌─────────────────────────────────────────────────────────────────┐    │  │
│  │  │ Date      Description              Amount        Account         │    │  │
│  │  ├─────────────────────────────────────────────────────────────────┤    │  │
│  │  │ May 15    Rent payment            -$1,500.00     Checking       │    │  │
│  │  │ May 10    New laptop              -$1,200.00     Credit Card    │    │  │
│  │  │ May 08    Flight to NYC          -$450.00       Credit Card    │    │  │
│  │  │ May 05    Monthly salary         +$5,000.00     Checking       │    │  │
│  │  │ May 03    Weekly groceries       -$85.00        Cash           │    │  │
│  │  │ (alternating rows, red for expense, green for income)        │    │  │
│  │  └─────────────────────────────────────────────────────────────────┘    │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                                                                  │
│  ┌───────────────────────────────────────────────────────────────────────────┐  │
│  │  Month-over-Month Trend                                               │  │
│  │                                                                       │  │
│  │   $8k ───────────────────────────────────────●                        │  │
│  │   $6k ────────────────────────────●────────────                       │  │
│  │   $4k ──────────────●────────────────────────────                       │  │
│  │   $2k ────●────────────────────────────────────────────────            │  │
│  │    $0 ──────────────────────────────────────────────                   │  │
│  │        Jan   Feb   Mar   Apr   May   Jun                               │  │
│  │                                                                       │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                                                                  │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Export Dropdown

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                                                                  │
│   Reports                               [◀]  May 2026  [▶]    [Export ▼]       │
│                                                                                  │
│                                                            ┌──────────────────┐│
│                                                            │ Download PDF    📄││
│                                                            ├──────────────────┤│
│                                                            │ Download CSV    📊││
│                                                            └──────────────────┘│
│                                                                                  │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## Design Tokens

### Colors
| Token | Hex | Usage |
|-------|-----|-------|
| `--income` | `#4CAF50` | Income values, income bars |
| `--expense` | `#D32F2F` | Expense values, expense bars |
| `--net-positive` | `#1976D2` | Positive net, trend line |
| `--net-negative` | `#D32F2F` | Negative net |
| `--bg-surface` | `#FFFFFF` | Card backgrounds |
| `--bg-page` | `#FAFAFA` | Page background |
| `--text-primary` | `#212121` | Main text |
| `--text-secondary` | `#757575` | Labels, secondary text |
| `--border` | `#E0E0E0` | Table borders |
| `--chart-legend-bg` | `#F5F5F5` | Legend background |
| `--chart-grid` | `#EEEEEE` | Chart grid lines |

### Chart Colors (Pie Slices)
| Category | Color |
|----------|-------|
| Food | `#FF5722` |
| Transportation | `#2196F3` |
| Shopping | `#9C27B0` |
| Bills | `#FF9800` |
| Entertainment | `#E91E63` |
| Health | `#00BCD4` |
| Other | `#607D8B` |

### Typography
| Token | Value | Usage |
|-------|-------|-------|
| `--font-display` | serif | Page title |
| `--font-body` | sans-serif | Body text |
| `--font-mono` | tabular-nums | Amounts |
| `--font-chart` | sans-serif | Chart labels |

### Spacing
| Token | Value | Usage |
|-------|-------|-------|
| `--card-padding` | 20px | Section padding |
| `--metric-gap` | 16px | Between metrics |
| `--chart-height` | 280px | Bar/pie chart heights |
| `--table-cell` | 48px | Table row height |

---

## Components

### 1. Page Header

| Element | Description |
|---------|-------------|
| Title | "Reports" (serif) |
| Month Nav | ◀ Month Year ▶ |
| Export | Dropdown button |

### 2. Summary Metrics

| Element | Description |
|---------|-------------|
| Layout | 4 columns grid |
| Values | Large numbers, tabular-nums |
| Icons | ↑ for income, ↓ for expense |
| Colors | Green income, red expense, blue net |

**Card Format**:
| Card | Format | Color |
|------|--------|-------|
| Income | "$X,XXX" + "Total Income" | Green |
| Expenses | "$X,XXX" + "Total Expenses" | Red |
| Net | "+/-$X,XXX" + "Net Balance" | Blue/Green/Red |
| Count | "XXX" + "Transactions" | Gray |

### 3. Income vs Expense Bar Chart

| Element | Description |
|---------|-------------|
| Type | Grouped bar chart |
| Period | Last 6 months |
| Bars | 2 bars per month (income + expense) |
| Colors | Green income, red expense |
| Labels | Month names below |
| Legend | Color indicators |

**Interactions**:
| Action | Behavior |
|--------|----------|
| Hover bar | Tooltip with exact values |
| Click bar | Navigate to that month's transactions |

### 4. Expense Breakdown Pie Chart

| Element | Description |
|---------|-------------|
| Type | Donut/pie chart |
| Center | Total amount |
| Segments | One per category |
| Colors | Category-specific |
| Legend | Right side, scrollable |

**Interactions**:
| Action | Behavior |
|--------|----------|
| Hover segment | Highlight + tooltip with amount/percentage |
| Click segment | Filter transactions by that category |
| Click legend item | Same as clicking segment |

### 5. Top 10 Transactions Table

| Element | Description |
|---------|-------------|
| Columns | Date, Description, Amount, Account |
| Sort | By amount (largest first) |
| Amount | Red for expense (-), green for income (+) |
| Rows | Alternating backgrounds |
| Amount | Tabular-nums, right-aligned |

### 6. Month-over-Month Trend Line

| Element | Description |
|---------|-------------|
| Type | Line chart with area fill |
| Period | Last 6 months |
| Line | Blue, solid |
| Points | Dot markers on data points |
| Fill | Light blue area below line |
| Y-axis | Dollar amounts |
| X-axis | Month names |

**Interactions**:
| Action | Behavior |
|--------|----------|
| Hover point | Tooltip with value |
| Hover line | Highlight trend |

### 7. Export Button + Dropdown

| Element | Description |
|---------|-------------|
| Button | "Export ▼" with download icon |
| Dropdown | PDF and CSV options |

**States**:
| State | Visual |
|-------|--------|
| Closed | Button only |
| Open | Dropdown visible |
| Exporting | Spinner on option |

---

## Interactions

### Page Load
1. Fetch current month data
2. Calculate totals
3. Fetch last 6 months for charts
4. Render all sections
5. Show loading states during fetch

### Month Navigation
1. User clicks ◀ or ▶
2. Update selected month
3. Refetch all data for new month
4. Update all charts and metrics

### Chart Hover
1. Show tooltip with exact values
2. Highlight hovered element
3. Update legend if applicable

### Pie Chart Click
1. Filter to show only transactions for that category
2. Navigate to transactions page with filter applied

### Export PDF Click
1. Open dropdown
2. Click "Download PDF"
3. Show loading spinner
4. Generate PDF (backend)
5. Download file

### Export CSV Click
1. Open dropdown
2. Click "Download CSV"
3. Show loading spinner
4. Generate CSV (backend)
5. Download file

### Bar Chart Click
1. Navigate to that month's transactions
2. URL: /transactions?year=2026&month=3

---

## API Contract

### Get Report Summary
```
GET /api/v1/transactions/report?year=2026&month=5

Response:
{
  "success": true,
  "result": {
    "year": 2026,
    "month": 5,
    "totalIncome": 845000,
    "totalExpense": 582000,
    "netBalance": 263000,
    "transactionCount": 47,
    "incomeCount": 5,
    "expenseCount": 42
  }
}
```

### Get Income/Expense Trends (6 months)
```
GET /api/v1/transactions/report/trends?months=6

Response:
{
  "success": true,
  "result": {
    "trends": [
      { "yearMonth": "202601", "income": 500000, "expense": 320000 },
      { "yearMonth": "202602", "income": 620000, "expense": 450000 },
      { "yearMonth": "202603", "income": 650000, "expense": 420000 },
      { "yearMonth": "202604", "income": 780000, "expense": 510000 },
      { "yearMonth": "202605", "income": 720000, "expense": 720000 },
      { "yearMonth": "202606", "income": 850000, "expense": 595000 }
    ]
  }
}
```

### Get Expense by Category
```
GET /api/v1/transactions/report/categories?year=2026&month=5

Response:
{
  "success": true,
  "result": {
    "categories": [
      { "categoryId": 5, "categoryName": "Food & Dining", "amount": 125000, "percentage": 21.5 },
      { "categoryId": 8, "categoryName": "Transportation", "amount": 89000, "percentage": 15.3 },
      ...
    ],
    "total": 582000
  }
}
```

### Get Top Transactions
```
GET /api/v1/transactions/report/top?year=2026&month=5&limit=10

Response:
{
  "success": true,
  "result": [
    { "id": 1, "date": 1716809400, "description": "Rent payment", "amount": -150000, "account": "Checking" },
    ...
  ]
}
```

### Export Report
```
GET /api/v1/transactions/report/export?format=pdf&year=2026&month=5
GET /api/v1/transactions/report/export?format=csv&year=2026&month=5

Response: Binary file download
Content-Type: application/pdf or text/csv
Content-Disposition: attachment; filename="report_2026_05.pdf"
```

---

## Backend Implementation Tasks

### Entity
- [ ] No new entities needed (reuse existing)

### Service
- [ ] `ReportService.getSummary(year, month)`
- [ ] `ReportService.getTrends(months)`
- [ ] `ReportService.getCategoryBreakdown(year, month)`
- [ ] `ReportService.getTopTransactions(year, month, limit)`
- [ ] `ReportService.exportPdf(year, month)`
- [ ] `ReportService.exportCsv(year, month)`

### Controller
- [ ] `GET /api/v1/transactions/report`
- [ ] `GET /api/v1/transactions/report/trends`
- [ ] `GET /api/v1/transactions/report/categories`
- [ ] `GET /api/v1/transactions/report/top`
- [ ] `GET /api/v1/transactions/report/export`

### DTO
- [ ] `ReportSummaryDto`
- [ ] `TrendDto`
- [ ] `CategoryBreakdownDto`
- [ ] `TopTransactionDto`

### PDF Generation (Optional)
- [ ] Use iText or similar for PDF
- [ ] Template-based PDF with charts

### CSV Generation
- [ ] Simple CSV with transactions data
- [ ] Include summary at top

### Tests
- [ ] Unit tests for calculations
- [ ] Integration tests

---

## Frontend Implementation Tasks

### Pages
- [ ] `reports.vue` — main reports page

### Components
- [ ] `ReportSummaryCards.vue` — 4 metric cards
- [ ] `SummaryCard.vue` — individual metric card
- [ ] `IncomeExpenseChart.vue` — bar chart
- [ ] `CategoryPieChart.vue` — pie/donut chart
- [ ] `TopTransactionsTable.vue` — transaction list
- [ ] `TrendLineChart.vue` — trend line
- [ ] `ExportDropdown.vue` — export button + dropdown
- [ ] `MonthNavigation.vue` — reuse from transactions

### Charts (ECharts)
- [ ] `useIncomeExpenseChart()` — grouped bar chart
- [ ] `useCategoryPieChart()` — donut chart
- [ ] `useTrendChart()` — line chart

### Composables
- [ ] `useReport()` — fetch all report data
- [ ] `useExport()` — export functionality

### Store
- [ ] `useReportStore()` — report state

### API
- [ ] GET /api/v1/transactions/report
- [ ] GET /api/v1/transactions/report/trends
- [ ] GET /api/v1/transactions/report/categories
- [ ] GET /api/v1/transactions/report/top
- [ ] GET /api/v1/transactions/report/export

### i18n Keys
- [ ] `reports.title` = "Reports"
- [ ] `reports.totalIncome` = "Total Income"
- [ ] `reports.totalExpenses` = "Total Expenses"
- [ ] `reports.netBalance` = "Net Balance"
- [ ] `reports.transactions` = "Transactions"
- [ ] `reports.incomeVsExpense` = "Income vs Expenses"
- [ ] `reports.expenseBreakdown` = "Expense Breakdown"
- [ ] `reports.topTransactions` = "Top Transactions"
- [ ] `reports.monthTrend` = "Month-over-Month Trend"
- [ ] `reports.export` = "Export"
- [ ] `reports.exportPdf` = "Download PDF"
- [ ] `reports.exportCsv` = "Download CSV"
- [ ] `reports.date` = "Date"
- [ ] `reports.description` = "Description"
- [ ] `reports.amount` = "Amount"
- [ ] `reports.account` = "Account"

---

## Edge Cases

| Case | Handling |
|------|----------|
| No transactions in month | Show $0 in metrics, empty charts |
| Income only, no expenses | Show $0 expenses |
| Expenses only, no income | Show $0 income, negative net |
| Very large amounts | Format with K/M suffixes |
| Future month | Disabled in navigation |
| PDF generation fails | Show error toast, retry |

---

## States Summary

| State | Visual |
|-------|--------|
| Loading | Skeleton loaders for all sections |
| Loaded | All charts and data visible |
| Empty Month | Zero values, empty state messages |
| Chart Hover | Tooltip with values |
| Export Loading | Spinner on export button |
| Export Error | Error toast |

---

## Responsive Behavior

| Breakpoint | Behavior |
|------------|----------|
| Desktop (>1200px) | Full layout, side-by-side charts |
| Tablet (768-1200px) | Stacked sections |
| Mobile (<768px) | Single column, swipeable charts |

---

*Last updated: 2026-05-22*
*Design complete — ready for implementation*