# Page Design - Accounts List

## Page Purpose
Manage all accounts (bank accounts, credit cards, cash, etc.) in one place.

## Route
`/accounts`

## Layout Structure

```
┌─────────────────────────────────────────────────────────────────┐
│ [☰]  Accounts                    [+ Add Account] [🔍] [⚙️]       │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│  [ All ] [ Cash ] [ Bank ] [ Credit ] [ Investment ] [ Archived ]│
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ Cash                      $2,500.00                     │    │
│  │ 💵 USD  •  Last: Today                                   │    │
│  │ ═══════════════════════════════════════════════════════ │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ Checking (Chase)        $5,200.50                     │    │
│  │ 🏦 USD  •  Last: 2 days ago                            │    │
│  │ ████████████░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░ │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                 │
│  ┌─────────────────────────────────────────────────────────┐    │
│  │ Credit Card (Visa)    -$1,200.00                      │    │
│  │ 💳 USD  •  Due: May 25                                  │    │
│  │ ▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░░ │    │
│  └─────────────────────────────────────────────────────────┘    │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

## Components

### Page Header
| Element | Description |
|---------|-------------|
| Title | "Accounts" |
| Add Button | Primary button to create new account |
| Search | Filter accounts by name |
| Settings | Account preferences |

### Filter Tabs
| Tab | Description |
|-----|-------------|
| All | All active accounts |
| Cash | Cash-type accounts |
| Bank | Checking/Savings accounts |
| Credit | Credit cards |
| Investment | Investment accounts |
| Archived | Hidden/archived accounts |

### Account Card
Each account displayed as a card with:
| Element | Description |
|---------|-------------|
| Icon | Based on account type |
| Name | Account name |
| Balance | Current balance (red if negative) |
| Currency | Currency code |
| Last Activity | Date of last transaction |
| Trend Bar | Visual spending/balance indicator |

## Account Types

| Type | Icon | Description |
|------|------|-------------|
| CASH | 💵 | Physical cash |
| CHECKING | 🏦 | Bank checking |
| SAVINGS | 🏪 | Savings account |
| CREDIT | 💳 | Credit card |
| INVESTMENT | 📈 | Stocks/brokerage |
| LOAN | 📋 | Loan account |
| DEBT | ⚠️ | Debt/liability |

## Data Model

### Account Card Data
```javascript
// GET /api/v1/accounts
{
  accounts: [
    {
      id: "1",
      name: "Cash",
      type: "CASH",
      currency: "USD",
      balance: 250000,      // $2,500.00
      icon: "wallet",
      color: "#4CAF50",
      includeInTotal: true,
      archived: false,
      lastTransactionTime: 1717104000
    },
    {
      id: "2", 
      name: "Checking (Chase)",
      type: "CHECKING",
      currency: "USD",
      balance: 520050,
      icon: "account_balance",
      color: "#2196F3",
      lastTransactionTime: 1717017600
    }
  ],
  totalBalance: 755050,    // $7,550.50
  assets: 755050,
  liabilities: -120000     // $1,200 credit balance
}
```

## Actions

### Card Actions
| Action | Trigger | Result |
|--------|---------|--------|
| View | Click card | Navigate to account detail |
| Edit | Edit icon on hover | Open edit modal |
| Archive | Archive icon on hover | Archive account |
| Delete | Long press / menu | Soft delete with confirmation |

### Page Actions
| Action | Description |
|--------|-------------|
| Add Account | Opens creation modal |
| Sort | By name, balance, type |
| Filter | By type, currency |
| Search | Text search by name |

## Modal: Create/Edit Account

```
┌─────────────────────────────────────┐
│ Create Account                   ✕  │
├─────────────────────────────────────┤
│                                     │
│  Name:                             │
│  ┌─────────────────────────────┐   │
│  │ My Account                  │   │
│  └─────────────────────────────┘   │
│                                     │
│  Type:                              │
│  ○ Cash  ○ Bank  ○ Credit  ○ Inv.   │
│                                     │
│  Currency: [ USD ▼ ]               │
│                                     │
│  Initial Balance:                   │
│  ┌─────────────────────────────┐   │
│  │ $ 0.00                      │   │
│  └─────────────────────────────┘   │
│                                     │
│  ☑ Include in total                │
│                                     │
│  Icon: [🎨]  Color: [#4CAF50]      │
│                                     │
│  Notes:                             │
│  ┌─────────────────────────────┐   │
│  │                             │   │
│  └─────────────────────────────┘   │
│                                     │
│  [ Cancel ]      [ Save ]          │
│                                     │
└─────────────────────────────────────┘
```

## Design Tokens
- Card: white bg, border-radius 12px, shadow
- Active tab: primary color underline
- Balance positive: #4CAF50
- Balance negative: #F44336
- Card padding: 16px

## i18n Keys
- `accounts.title` = "Accounts"
- `accounts.add` = "Add Account"
- `accounts.search` = "Search accounts"
- `accounts.type.all` = "All"
- `accounts.type.cash` = "Cash"
- `accounts.type.bank` = "Bank"
- `accounts.type.credit` = "Credit"
- `accounts.type.investment` = "Investment"
- `accounts.type.archived` = "Archived"

## OpenDesign Reference
Create accounts list page with:
- Header with add button
- Filter tabs by account type
- Account cards with balance and trend
- Hover actions for edit/archive