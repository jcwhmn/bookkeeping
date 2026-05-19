# Page Design - Transactions List

## Page Purpose
List and search all transactions with filtering and bulk actions.

## Route
`/transactions`

## Layout Structure

```
┌─────────────────────────────────────────────────────────────────┐
│ [☰]  Transactions              [+ Add] [📅 May 2026 ▼] [⚙️]    │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  [ Expense ] [ Income ] [ Transfer ]            [🔍 Search...]  │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐   │
│  │ Date: May 19, 2026                                     │   │
│  ├─────────────────────────────────────────────────────────┤   │
│  │ 🍔 Food - Groceries         -$85.00    [Cash]          │   │
│  │        Weekly groceries at Costco                     │   │
│  │                                    [food] [groceries]   │   │
│  ├─────────────────────────────────────────────────────────┤   │
│  │ 🚗 Transport                   -$45.00    [Checking]     │   │
│  │        Uber ride                                      │   │
│  │                                              [travel]   │   │
│  ├─────────────────────────────────────────────────────────┤   │
│  │ Date: May 18, 2026                                     │   │
│  ├─────────────────────────────────────────────────────────┤   │
│  │ 💰 Salary                   +$3,000.00    [Bank]       │   │
│  │        Monthly salary                                    │   │
│  │                                              [income]   │   │
│  ├─────────────────────────────────────────────────────────┤   │
│  │ 🔄 Transfer                    -$500.00    [Cash]      │   │
│  │        To Checking                  [→ Checking]        │   │
│  └─────────────────────────────────────────────────────────┘   │
│                                                                 │
│                    Showing 1-20 of 156                          │
│                    [←] 1 2 3 4 5 ... 8 [→]                     │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Components

### Page Header
| Element | Description |
|---------|-------------|
| Title | "Transactions" |
| Add Button | Quick add transaction |
| Period Selector | Filter by month/year |
| Settings | View preferences |

### Filter Bar
| Element | Description |
|---------|-------------|
| Type Tabs | Expense / Income / Transfer / All |
| Search | Full-text search |
| Account Filter | Dropdown to filter by account |
| Category Filter | Dropdown to filter by category |
| Date Range | From - To date picker |

### Transaction List
Grouped by date (newest first)

### Transaction Row
| Element | Description |
|---------|-------------|
| Category Icon | Icon for transaction category |
| Title | Category + subcategory or notes |
| Amount | Positive (green) or negative (red) |
| Account Badge | Source account name |
| Notes | Truncated description |
| Tags | Tag chips |
| Transfer indicator | Shows destination account for transfers |

### Pagination
- Cursor-based pagination
- "Showing X-Y of Z"
- Load more on scroll

## Grouping Logic

```
Today
├── Transaction 1
├── Transaction 2
Yesterday
├── Transaction 3
May 18, 2026
├── Transaction 4
├── Transaction 5
May 2026
├── Transaction 6
...
```

## Filter States

### By Type
```
All | Expense | Income | Transfer
```

### By Account
```
[All Accounts ▼]
├── All Accounts
├── Cash
├── Checking (Chase)
└── Credit Card
```

### By Category
```
[All Categories ▼]
├── Income
│   ├── Salary
│   ├── Bonus
│   └── Investment
└── Expense
    ├── Food
    │   ├── Groceries
    │   └── Restaurants
    └── Transport
```

## Data Model

### List Response
```javascript
// GET /api/v1/transactions?type=EXPENSE&accountId=1&limit=20&cursor=...
{
  transactions: [
    {
      id: "123",
      type: "EXPENSE",
      amount: "-8500",
      currency: "USD",
      accountId: "1",
      accountName: "Cash",
      destinationAccountId: null,
      destinationAccountName: null,
      categoryId: "5",
      categoryName: "Groceries",
      categoryIcon: "shopping_cart",
      transactionTime: "1717104000",
      notes: "Weekly groceries at Costco",
      tags: ["food", "groceries"],
      relatedTransactionId: null
    }
  ],
  pagination: {
    nextCursor: "eyJ0IjoxNzE3MTA0MDAwfQ==",
    hasMore: true,
    total: 156
  },
  summary: {
    totalIncome: 300000,
    totalExpense: 8500,
    netChange: 291500
  }
}
```

### Batch Operations
| Operation | Description |
|-----------|-------------|
| Delete | Remove selected transactions |
| Categorize | Assign category to selected |
| Tag | Add/remove tags from selected |
| Export | Export selected to CSV/Excel |

## Search

### Search Fields
- Notes content
- Category name
- Account name
- Tags

### Search Behavior
- Debounced input (300ms)
- Minimum 2 characters
- Highlights matching text

## Quick Actions

| Action | Keyboard |
|--------|----------|
| Add Transaction | Ctrl/Cmd + N |
| Search | Ctrl/Cmd + F |
| Select All | Ctrl/Cmd + A |
| Delete Selected | Delete key |

## Design Tokens
- List row height: 72px
- Group header: sticky, gray bg
- Amount font: 16px, semibold
- Account badge: small, gray bg
- Tag chips: small, colored bg

## i18n Keys
- `transactions.title` = "Transactions"
- `transactions.add` = "Add Transaction"
- `transactions.search` = "Search transactions"
- `transactions.filter.all` = "All"
- `transactions.filter.expense` = "Expense"
- `transactions.filter.income` = "Income"
- `transactions.filter.transfer` = "Transfer"
- `transactions.noResults` = "No transactions found"
- `transactions.loadMore` = "Load More"

## OpenDesign Reference
Create transactions list page with:
- Filter bar with type tabs
- Search input
- Grouped transaction list
- Date headers
- Row actions on hover
- Pagination controls