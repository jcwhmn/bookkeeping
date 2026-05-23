# Statistics Page — Design Spec

**Date**: 2026-05-22
**Source**: Open Design
**Status**: ✅ Designed

---

## Wireframe

### Statistics Page Overview

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                                                                                  │
│   Statistics                      [◀]  May 2026  [▶]                            │
│                                                                                  │
│  ┌───────────────────────────────────────────────────────────────────────────┐  │
│  │  [This Month] [Last Month] [Last 3 Months] [Last 6 Months] [Custom ▼]   │  │
│  │  (blue highlight on active)                                              │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                                                                  │
│  ┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐               │
│  │ Total Income     │ │ Total Expenses   │ │ Net Balance      │               │
│  │ ↑ $8,450.00      │ │ ↓ $5,820.00      │ │ ± $2,630.00      │               │
│  │ (green)          │ │ (red)            │ │ (blue)          │               │
│  └──────────────────┘ └──────────────────┘ └──────────────────┘               │
│                                                                                  │
│  ┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐               │
│  │ Avg Transaction   │ │ Largest Expense   │ │ Largest Income   │               │
│  │ $186.17           │ │ Restaurant       │ │ Salary           │               │
│  │                  │ │ -$342.18         │ │ +$5,000.00       │               │
│  └──────────────────┘ └──────────────────┘ └──────────────────┘               │
│                                                                                  │
│  ┌───────────────────────────────────────────────────────────────────────────┐  │
│  │  Spending by Category                                                     │  │
│  │                                                                           │  │
│  │  Food & Dining         ████████████████████████████████████  $1,245 (25%) │  │
│  │  Transportation        ████████████████████████████         $890 (18%)    │  │
│  │  Shopping             ██████████████████████              $756 (15%)      │  │
│  │  Utilities            ████████████████                    $520 (10%)      │  │
│  │  Entertainment        ███████████████                     $445 (9%)       │  │
│  │  Healthcare           ██████████████                      $380 (7%)       │  │
│  │                                                                           │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                                                                  │
│  ┌───────────────────────────────────────────────────────────────────────────┐  │
│  │  Income vs Expense Trend                                                  │  │
│  │                                                                           │  │
│  │   $10k ────────────────────────────────────────● (income)                  │  │
│  │         ████████                            ████████                       │  │
│  │   $8k   ████████        ████████            ████████                       │  │
│  │         ████████        ████████  ──────────████████                       │  │
│  │   $6k   ████████ ──────██████              ████████ (expense)              │  │
│  │         ████████        ████████            ████████                        │  │
│  │   $4k ──██████          ████████            ████████                        │  │
│  │         ████████        ████████            ████████                        │  │
│  │   $2k ──██████ ────────██████              ████████                        │  │
│  │    $0 ──────────────────────────────────────────────                        │  │
│  │        Jan    Feb    Mar    Apr    May    Jun                            │  │
│  │                                                                           │  │
│  │  Legend: [■ Green Income] [■ Red Expenses]                               │  │
│  │                                                                           │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                                                                  │
│  ┌───────────────────────────────────────────────────────────────────────────┐  │
│  │  Spending Heatmap                                          Legend        │  │
│  │  ┌───────────────────────────────────────────────────────────────────┐  │  │
│  │  │ Jun Jul Aug Sep Oct Nov Dec Jan Feb Mar Apr May                    │  │  │
│  │  │    ░░  ░░  ░░  ░░  ░░  ░░  ░░  ░░  ░░  ░░  ██                      │  │  │
│  │  │    ░░  ░░  ██  ░░  ░░  ░░  ░░  ██  ░░  ░░  ██  ██                  │  │  │
│  │  │    ░░  ██  ░░  ░░  ██  ░░  ░░  ░░  ░░  ██  ██  ██  ██              │  │  │
│  │  │    ░░  ░░  ░░  ██  ░░  ░░  ░░  ░░  ██  ░░  ██  ██  ██              │  │  │
│  │  │    ░░  ░░  ░░  ░░  ░░  ██  ░░  ░░  ░░  ░░  ░░  ██  ██  ██          │  │  │
│  │  │    ██  ░░  ░░  ░░  ░░  ░░  ░░  ██  ░░  ░░  ░░  ░░  ░░  ██          │  │  │
│  │  └───────────────────────────────────────────────────────────────────┘  │  │
│  │                                                                         │  │  │
│  │  $0     $1-50    $51-100   $101-200   $200+                            │  │  │
│  │  ░░      ░░        ░░        ██         ███                             │  │  │
│  │  (white) (light)  (medium) (dark)   (darkest)                         │  │  │
│  │                                                                         │  │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                                                                  │
│  ┌───────────────────────────────────────────────────────────────────────────┐  │
│  │  Top 5 Categories                   Insights                              │  │
│  │  ┌───────────────────────────┐    ┌────────────────────────────────┐   │  │
│  │  │                           │    │ 💡 You spent 15% more on Dining │   │  │
│  │  │        Food               │    │    this month compared to last  │   │  │
│  │  │         25%              │    └────────────────────────────────┘   │  │
│  │  │                           │    ┌────────────────────────────────┐   │  │
│  │  │   Transport    Shop      │    │ 💡 Consider setting a budget   │   │  │
│  │  │     18%         15%       │    │    for Shopping                │   │  │
│  │  │                           │    └────────────────────────────────┘   │  │
│  │  │             $5,820        │    ┌────────────────────────────────┐   │  │
│  │  │              Total        │    │ 💡 Your net balance is positive│   │  │
│  │  └───────────────────────────┘    │    for the 3rd consecutive    │   │  │
│  │                                   │    month                        │   │  │
│  │  Legend:                    │    └────────────────────────────────┘   │  │
│  │  ■ Food (25%)                      (blue info cards)                 │  │
│  │  ■ Transport (18%)                                               │  │
│  │  ■ Shopping (15%)                                                 │  │
│  │  ■ Utilities (10%)                                                │  │
│  │  ■ Other (32%)                                                    │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                                                                  │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### Custom Date Picker

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│  [This Month] [Last Month] [Last 3 Months] [Last 6 Months] [Custom ▼]         │
│                                                                                  │
│  ┌───────────────────────────────────────────────────────────────────────────┐  │
│  │  Custom Date Range                                                 │  │
│  │  ┌─────────────────────┐    ┌─────────────────────┐                  │  │
│  │  │ From: May 1, 2026  │    │ To: May 22, 2026   │                  │  │
│  │  └─────────────────────┘    └─────────────────────┘                  │  │
│  │                           [ Apply ]                                   │  │
│  └───────────────────────────────────────────────────────────────────────────┘  │
│                                                                                  │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## Design Tokens

### Colors
| Token | Hex | Usage |
|-------|-----|-------|
| `--income` | `#4CAF50` | Income values, lines |
| `--expense` | `#D32F2F` | Expense values, lines |
| `--net-positive` | `#1976D2` | Positive net |
| `--net-negative` | `#D32F2F` | Negative net |
| `--primary` | `#1976D2` | Active buttons, links |
| `--primary-light` | `#E3F2FD` | Active button background |
| `--insight-bg` | `#E3F2FD` | Insight card background |
| `--insight-border` | `#90CAF9` | Insight card border |
| `--bg-surface` | `#FFFFFF` | Card backgrounds |
| `--bg-page` | `#FAFAFA` | Page background |
| `--text-primary` | `#212121` | Main text |
| `--text-secondary` | `#757575` | Labels |
| `--border` | `#E0E0E0` | Card borders |

### Heatmap Colors
| Level | Amount | Color |
|-------|--------|-------|
| 0 | $0 | `#FFFFFF` |
| 1 | $1-50 | `#C8E6C9` |
| 2 | $51-100 | `#81C784` |
| 3 | $101-200 | `#4CAF50` |
| 4 | $200+ | `#1B5E20` |

### Typography
| Token | Value | Usage |
|-------|-------|-------|
| `--font-display` | serif | Page title |
| `--font-body` | sans-serif | Body text |
| `--font-mono` | tabular-nums | Amounts |

### Spacing
| Token | Value | Usage |
|-------|-------|-------|
| `--card-padding` | 20px | Section padding |
| `--metric-gap` | 16px | Between metric cards |
| `--chart-height` | 300px | Chart heights |
| `--heatmap-cell` | 12px | Heatmap cell size |

---

## Components

### 1. Page Header

| Element | Description |
|---------|-------------|
| Title | "Statistics" (serif) |
| Date Nav | ◀ Month Year ▶ |

### 2. Date Range Quick Buttons

| Element | Description |
|---------|-------------|
| Options | This Month, Last Month, Last 3 Months, Last 6 Months, Custom |
| Active | Blue filled background |
| Custom | Shows dropdown when selected |

**States**:
| State | Visual |
|-------|--------|
| Default | Gray text, no background |
| Hover | Light blue background |
| Active | Blue background, white text |
| Custom Open | Shows date picker below |

### 3. Custom Date Picker

| Element | Description |
|---------|-------------|
| Inputs | From and To date fields |
| Apply | Blue button |
| Visibility | Only shown when Custom selected |

### 4. Summary Metric Card

| Element | Description |
|---------|-------------|
| Title | Metric name (small, gray) |
| Value | Large amount |
| Subtitle | Description or category name |
| Color | Based on metric type |

**Types**:
| Metric | Color | Icon |
|--------|-------|------|
| Total Income | Green | ↑ |
| Total Expenses | Red | ↓ |
| Net Balance | Blue/Green/Red | ± |
| Avg Transaction | Gray | — |
| Largest Expense | Red | — |
| Largest Income | Green | — |

### 5. Category Bar Chart (Horizontal)

| Element | Description |
|---------|-------------|
| Type | Horizontal bar chart |
| Bars | Colored based on category |
| Label | Category name left |
| Bar | Width proportional to amount |
| Amount | Dollar amount + percentage right |

**Categories with Colors**:
| Category | Color |
|----------|-------|
| Food & Dining | `#FF5722` |
| Transportation | `#2196F3` |
| Shopping | `#9C27B0` |
| Utilities | `#FF9800` |
| Entertainment | `#E91E63` |
| Healthcare | `#00BCD4` |
| Other | `#607D8B` |

### 6. Income vs Expense Trend Chart

| Element | Description |
|---------|-------------|
| Type | Area line chart |
| Lines | Green (income), Red (expense) |
| Fill | Transparent area below line |
| Labels | Month names |
| Y-axis | Dollar amounts |

**Interactions**:
| Action | Behavior |
|--------|----------|
| Hover | Tooltip with values |
| Legend click | Toggle line visibility |

### 7. Spending Heatmap Calendar

| Element | Description |
|---------|-------------|
| Type | GitHub-style contribution calendar |
| Grid | 12 months × ~30 days |
| Colors | Green intensity based on spend |
| Legend | $0, $1-50, $51-100, $101-200, $200+ |
| Hover | Tooltip with date + amount |

**Cell States**:
| State | Visual |
|-------|--------|
| None | White (#FFFFFF) |
| Low | Light green (#C8E6C9) |
| Medium | Medium green (#81C784) |
| High | Dark green (#4CAF50) |
| Highest | Darkest green (#1B5E20) |
| Hover | Highlighted cell, tooltip |

### 8. Top 5 Categories Pie Chart

| Element | Description |
|---------|-------------|
| Type | Donut chart |
| Center | Total amount |
| Segments | Category colors |
| Legend | Right side with percentages |

### 9. Insight Cards

| Element | Description |
|---------|-------------|
| Background | Light blue |
| Icon | 💡 lightbulb |
| Text | Insight message |
| Style | Rounded corners, subtle shadow |

**Example Insights**:
- "You spent 15% more on Dining this month"
- "Consider setting a budget for Shopping"
- "Your net balance is positive for 3 consecutive months"
- "Highest spending day was May 15 with $342"

### 10. Legend

| Element | Description |
|---------|-------------|
| Heatmap Legend | Color scale with amounts |
| Chart Legend | Color + label pairs |

---

## Interactions

### Page Load
1. Fetch current month data by default
2. Calculate all metrics
3. Fetch historical data for charts
4. Generate insights
5. Render all sections

### Quick Button Click
1. Set active button
2. Calculate date range
3. Fetch data for range
4. Update all sections

### Custom Date Apply
1. Validate date range
2. Fetch data for range
3. Update all sections

### Chart Hover
1. Show tooltip with exact values
2. Highlight relevant element

### Heatmap Cell Hover
1. Highlight cell
2. Show tooltip: "May 15: $342.18"

### Heatmap Cell Click
1. Navigate to that day's transactions
2. URL: /transactions?date=2026-05-15

---

## API Contract

### Get Statistics Summary
```
GET /api/v1/transactions/statistics?start_date=2026-05-01&end_date=2026-05-31

Response:
{
  "success": true,
  "result": {
    "totalIncome": 845000,
    "totalExpense": 582000,
    "netBalance": 263000,
    "avgTransaction": 18617,
    "largestExpense": { "amount": 34218, "description": "Restaurant" },
    "largestIncome": { "amount": 500000, "description": "Salary" },
    "transactionCount": 47
  }
}
```

### Get Category Breakdown
```
GET /api/v1/transactions/statistics/categories?start_date=...&end_date=...

Response:
{
  "success": true,
  "result": [
    { "categoryId": 5, "categoryName": "Food & Dining", "amount": 124500, "percentage": 21.4 },
    ...
  ]
}
```

### Get Trends
```
GET /api/v1/transactions/statistics/trends?months=6

Response:
{
  "success": true,
  "result": {
    "trends": [
      { "yearMonth": "202601", "income": 500000, "expense": 320000 },
      ...
    ]
  }
}
```

### Get Daily Spending (for Heatmap)
```
GET /api/v1/transactions/statistics/daily?start_date=...&end_date=...

Response:
{
  "success": true,
  "result": {
    "daily": [
      { "date": "2026-05-01", "amount": 0 },
      { "date": "2026-05-02", "amount": 8500 },
      ...
    ]
  }
}
```

### Get Insights
```
GET /api/v1/transactions/statistics/insights?year=2026&month=5

Response:
{
  "success": true,
  "result": [
    { "type": "spending_change", "message": "You spent 15% more on Dining..." },
    { "type": "budget_recommendation", "message": "Consider setting a budget for Shopping" },
    { "type": "positive_trend", "message": "Your net balance is positive..." }
  ]
}
```

---

## Backend Implementation Tasks

### Service
- [ ] `StatisticsService.getSummary(startDate, endDate)`
- [ ] `StatisticsService.getCategoryBreakdown(startDate, endDate)`
- [ ] `StatisticsService.getDailySpending(startDate, endDate)`
- [ ] `StatisticsService.getInsights(year, month)`
- [ ] `StatisticsService.generateInsights(data)`

### Controller
- [ ] `GET /api/v1/transactions/statistics`
- [ ] `GET /api/v1/transactions/statistics/categories`
- [ ] `GET /api/v1/transactions/statistics/daily`
- [ ] `GET /api/v1/transactions/statistics/insights`
- [ ] `GET /api/v1/transactions/statistics/trends`

### DTO
- [ ] `StatisticsSummaryDto`
- [ ] `DailySpendingDto`
- [ ] `InsightDto`

### Insights Logic
- [ ] Compare to previous period
- [ ] Generate spending change insights
- [ ] Budget recommendations based on categories
- [ ] Trend analysis (consecutive positive months)

### Tests
- [ ] Unit tests for insight generation
- [ ] Integration tests

---

## Frontend Implementation Tasks

### Pages
- [ ] `statistics.vue` — main statistics page

### Components
- [ ] `DateRangeButtons.vue` — quick buttons
- [ ] `CustomDatePicker.vue` — date range picker
- [ ] `MetricCard.vue` — summary metric card
- [ ] `CategoryBarChart.vue` — horizontal bar chart
- [ ] `TrendChart.vue` — income/expense trend
- [ ] `SpendingHeatmap.vue` — calendar heatmap
- [ ] `TopCategoriesPie.vue` — donut chart
- [ ] `InsightCard.vue` — insight display
- [ ] `MonthNavigation.vue` — reuse component

### Charts (ECharts)
- [ ] `useCategoryBarChart()` — horizontal bars
- [ ] `useTrendChart()` — area line chart
- [ ] `useTopCategoriesChart()` — donut chart
- [ ] Custom heatmap (may need canvas or d3)

### Composables
- [ ] `useStatistics()` — fetch and cache data
- [ ] `useDateRange()` — date range state
- [ ] `useInsights()` — insights logic

### Store
- [ ] `useStatisticsStore()` — statistics state

### API
- [ ] GET /api/v1/transactions/statistics
- [ ] GET /api/v1/transactions/statistics/categories
- [ ] GET /api/v1/transactions/statistics/daily
- [ ] GET /api/v1/transactions/statistics/insights
- [ ] GET /api/v1/transactions/statistics/trends

### i18n Keys
- [ ] `statistics.title` = "Statistics"
- [ ] `statistics.thisMonth` = "This Month"
- [ ] `statistics.lastMonth` = "Last Month"
- [ ] `statistics.last3Months` = "Last 3 Months"
- [ ] `statistics.last6Months` = "Last 6 Months"
- [ ] `statistics.custom` = "Custom"
- [ ] `statistics.totalIncome` = "Total Income"
- [ ] `statistics.totalExpenses` = "Total Expenses"
- [ ] `statistics.netBalance` = "Net Balance"
- [ ] `statistics.avgTransaction` = "Avg Transaction"
- [ ] `statistics.largestExpense` = "Largest Expense"
- [ ] `statistics.largestIncome` = "Largest Income"
- [ ] `statistics.spendingByCategory` = "Spending by Category"
- [ ] `statistics.incomeVsExpenseTrend` = "Income vs Expense Trend"
- [ ] `statistics.spendingHeatmap` = "Spending Heatmap"
- [ ] `statistics.topCategories` = "Top Categories"
- [ ] `statistics.insights` = "Insights"
- [ ] `statistics.from` = "From"
- [ ] `statistics.to` = "To"
- [ ] `statistics.apply` = "Apply"

---

## Edge Cases

| Case | Handling |
|------|----------|
| No transactions | Show $0, empty charts, no insights |
| Single day selected | Heatmap shows one cell |
| Very large amounts | Format with K/M |
| Future date range | Disabled in picker |
| Insights unavailable | Show "Not enough data" |

---

## States Summary

| State | Visual |
|-------|--------|
| Loading | Skeleton loaders for all sections |
| Loaded | All charts and data visible |
| Date Changing | Brief loading state |
| Chart Hover | Tooltip visible |
| Heatmap Hover | Cell highlighted, tooltip |

---

## Responsive Behavior

| Breakpoint | Behavior |
|------------|----------|
| Desktop (>1200px) | Full layout |
| Tablet (768-1200px) | 2-column metric cards |
| Mobile (<768px) | Single column, stacked charts |

---

*Last updated: 2026-05-22*
*Design complete — ready for implementation*