# Page Design - Dashboard

## Page Purpose
Main view after login. Shows financial overview with key metrics and recent activity.

## Route
`/` (after login)

## Layout Structure

```
┌─────────────────────────────────────────────────────────────────┐
│ [☰]  Bookkeeping                      [💰 USD ▼] [🔔] [👤 ▼]   │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐        │
│  │ Assets   │  │ Liabil.  │  │  Net     │  │ This Mo. │        │
│  │ $12,500  │  │ -$2,000  │  │ $10,500  │  │ Income   │        │
│  │ ▲ 5.2%   │  │ ▼ 2.1%   │  │ ▲ 8.3%   │  │ $3,200   │        │
│  └──────────┘  └──────────┘  └──────────┘  └──────────┘        │
│                                                                 │
│  ┌─────────────────────────────────────┐  ┌────────────────┐  │
│  │ Recent Transactions                 │  │ Budget Status  │  │
│  │                                      │  │                │  │
│  │ [🍔] Food - Groceries   -$85.00     │  │ Food ████░░ 80%│  │
│  │ [🚗] Transport           -$45.00     │  │ Util  ██░░░░ 40%│  │
│  │ [💰] Salary            +$3,000.00    │  │ Shop █░░░░░ 20%│  │
│  │ [🏠] Housing           -$1,200.00    │  │                │  │
│  │                                      │  │                │  │
│  │ [View All →]                        │  │                │  │
│  └─────────────────────────────────────┘  └────────────────┘  │
│                                                                 │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │              Balance Trend (Last 30 Days)              │   │
│  │  $12k ───────────────────────────────────────────────   │   │
│  │         ╭──╮                                           │   │
│  │  $11k ─╯   ╰────────────────────╮                     │   │
│  │                                  ╰───────►            │   │
│  └──────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Components

### Header Bar
| Element | Description |
|---------|-------------|
| Menu Icon | Opens sidebar navigation |
| App Title | "Bookkeeping" |
| Currency Selector | Dropdown to switch display currency |
| Notifications | Bell icon with badge count |
| User Menu | Avatar with dropdown (Profile, Settings, Logout) |

### Summary Cards (4 cards in row)
| Card | Data | Notes |
|------|------|-------|
| Assets | Total positive balance | Sum of all accounts |
| Liabilities | Total negative balance | Accounts with negative balance |
| Net Worth | Assets + Liabilities | Key financial metric |
| This Month Income/Expense | Net for current month | Income vs Expense comparison |

### Recent Transactions Panel
- List of last 5 transactions
- Each row: icon, description, amount, date
- "View All" link to transactions page

### Budget Status Panel
- Top 3 budget categories
- Progress bar showing spent vs budget
- Color coding: green (<60%), yellow (60-80%), red (>80%)

### Balance Trend Chart
- Line chart showing balance over time
- X-axis: Last 30 days
- Y-axis: Balance amount
- Hover to show exact values

## Data Requirements

### Summary Metrics
```javascript
{
  assets: 1250000,    // $12,500.00 in cents
  liabilities: -200000, // -$2,000.00
  netWorth: 1050000,   // $10,500.00
  monthIncome: 320000, // $3,200.00
  monthExpense: 285000 // $2,850.00
}
```

### Recent Transactions
```javascript
// GET /api/v1/transactions?limit=5
{
  transactions: [
    {
      id: "123",
      type: "EXPENSE",
      amount: "-8500",      // -$85.00
      accountName: "Cash",
      categoryName: "Groceries",
      transactionTime: "1717104000"
    }
  ]
}
```

### Budget Status
```javascript
// GET /api/v1/budgets?limit=3
{
  budgets: [
    {
      categoryName: "Food",
      spent: 80000,    // $800
      budget: 100000,  // $1000
      percentage: 80
    }
  ]
}
```

## Design Tokens
- Card shadow: 0 2px 8px rgba(0,0,0,0.08)
- Summary cards: white background, border-radius 12px
- Positive amount: #4CAF50 (green)
- Negative amount: #F44336 (red)
- Progress bar height: 8px

## i18n Keys
- `dashboard.title` = "Dashboard"
- `dashboard.assets` = "Assets"
- `dashboard.liabilities` = "Liabilities"
- `dashboard.netWorth` = "Net Worth"
- `dashboard.thisMonth` = "This Month"
- `dashboard.recentTransactions` = "Recent Transactions"
- `dashboard.budgetStatus` = "Budget Status"
- `dashboard.viewAll` = "View All"
- `dashboard.income` = "Income"
- `dashboard.expense` = "Expense"

## OpenDesign Reference
Create dashboard page with:
- Header bar with user menu
- 4 summary metric cards
- Recent transactions list
- Budget status sidebar
- Line chart for balance trend