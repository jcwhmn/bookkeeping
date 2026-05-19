# Bookkeeping Requirements Specification v1.0

## Project Overview

- **Project Name**: Bookkeeping
- **Version**: 1.0.0
- **Date**: 2026-05-19
- **Status**: Draft (Brainstorming in Progress)

---

## 1. Target Users

| User Type | Description |
|-----------|-------------|
| Primary | Small team (~20 members, estimate) |
| Secondary | Family members tracking personal expenses |

### User Stories

**AS** a team member, **I WANT** to record my personal income and expenses,
**SO THAT** I can track where my money goes and manage my finances.

**AS** a team member, **I WANT** to see my spending by category and trends,
**SO THAT** I can make better financial decisions.

**AS** a team member, **I WANT** to set budget limits and get alerts,
**SO THAT** I don't overspend unintentionally.

---

## 2. Scope for v1.0

### Included (Core Domain)

| Module | Priority | Description |
|--------|----------|-------------|
| Account Management | P0 | All account types including Alipay, WeChat, bank accounts |
| Transaction Management | P0 | Manual entry, recurring templates |
| Category Management | P0 | Hierarchical income/expense categories |
| Tag Management | P1 | Tags for cross-cutting categorization |
| Transaction Templates | P1 | Recurring scheduled transactions |
| Statistics & Reports | P0 | Monthly summaries, category breakdown, cash flow |
| Budget Management | P1 | Per-category spending limits with alerts |
| Multi-currency | P1 | Track multiple currencies (e.g., USD, CNY) |
| User Management | P0 | User registration, login, profile |
| Authentication | P0 | Username/password, JWT session |

### Excluded from v1.0

| Module | Reason |
|--------|--------|
| OAuth/OIDC login | Nice to have later |
| 2FA | Security add-on |
| Bank feed auto-import | Technical complexity |
| Bank reconciliation | Manual matching is sufficient for v1 |
| Insights Explorer | Advanced feature |
| File/Picture attachments | Not required per user feedback |
| AI/LLM features | Out of scope |
| MCP protocol | Out of scope |

---

## 3. Account Types

Support the following account types:

| Type | Examples | Notes |
|------|----------|-------|
| Cash | Wallet, pocket cash | Physical currency |
| Bank | Checking, savings | Traditional banking |
| Credit Card | Visa, Mastercard | Liability account |
| Investment | Stocks, funds, bonds | Asset account |
| Mobile Payment | Alipay, WeChat Pay, Venmo | E-wallet platforms |
| Loyalty/Points | Airline miles, hotel points | Point-based |
| Other | Custom accounts | User-defined |

### Account Properties

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| name | String | Yes | Account name |
| type | Enum | Yes | See account types above |
| currency | String | Yes | ISO 4217 (USD, CNY, EUR...) |
| balance | Decimal | Yes | Current balance (auto-calculated from transactions) |
| icon | String | No | Icon identifier |
| color | String | No | Hex color for UI |
| notes | String | No | User notes |
| includeInTotal | Boolean | Yes | Show in dashboard total (default: true) |
| archived | Boolean | Yes | Hide from active list (default: false) |
| ownerId | String | Yes | Owner user ID |
| sharedWith | Array | No | List of user IDs with access |

---

## 4. Transaction Types

| Type | Direction | Examples |
|------|-----------|----------|
| Income | + | Salary, bonus, refund |
| Expense | - | Groceries, transport, utilities |
| Transfer | 0 | Move money between accounts |
| Modify Balance | +/- | Correction, interest |

---

## 5. Category System

### Hierarchical Structure

```
Income
├── Salary
│   ├── Monthly Salary
│   └── Bonus
├── Investment
│   ├── Dividends
│   └── Interest
└── Other Income

Expense
├── Food
│   ├── Groceries
│   └── Restaurants
├── Transport
│   ├── Public Transit
│   └── Fuel
├── Housing
│   ├── Rent/Mortgage
│   └── Utilities
├── Entertainment
├── Healthcare
└── ...
```

### Default Categories (Pre-seeded)

**Income:**
- Salary, Bonus, Investment, Refund, Gift, Other

**Expense:**
- Food (Groceries, Restaurants), Transport, Housing, Utilities, Healthcare, Entertainment, Shopping, Education, Insurance, Taxes, Other

---

## 6. Tag System

Tags are optional labels for transactions.

| Field | Type | Description |
|-------|------|-------------|
| name | String | Tag name (unique per user) |
| color | String | Hex color |
| icon | String | Icon identifier |

**Use Cases:**
- Mark business vs personal expenses
- Track tax-deductible items (#tax-deductible)
- Flag pending reimbursement (#reimburse-to-client)

---

## 7. Transaction Templates

For recurring transactions:

| Field | Type | Description |
|-------|------|-------------|
| name | String | Template name |
| type | Enum | INCOME, EXPENSE, TRANSFER |
| amount | Decimal | Transaction amount |
| currency | String | ISO 4217 |
| accountId | String | Target account |
| categoryId | String | Category |
| tagIds | Array | Tags |
| notes | String | Default notes |
| schedule | Object | Frequency + next run date |
| enabled | Boolean | Active/inactive |

**Schedule Options:**
- Daily, Weekly, Monthly, Yearly
- Custom interval (every N days)

### Transaction Entry

| Mode | Description |
|------|-------------|
| Quick Entry | Minimal fields: amount, category, account. Fast for daily use. |
| Detailed Form | All fields: notes, tags, date, time, photos. Full editing. |

Users can switch between modes seamlessly - quick entry has "Edit" button to expand.

---

## 8. Budget Management

Per-category spending limits:

| Field | Type | Description |
|-------|------|-------------|
| categoryId | String | Category reference |
| amount | Decimal | Budget limit |
| period | Enum | Monthly, Weekly, Yearly, or Custom |
| customPeriodStart | Int | Day of month for custom period (1-28) |
| rollover | Boolean | Unused budget carries over |
| alertThreshold | Decimal | Alert when % used (default: 80%) |

**Alert Behavior:**
- When transaction makes category reach threshold --> push notification
- Visual indicator on dashboard

---

## 9. Multi-Currency Support

### Requirements

| Feature | Description |
|---------|-------------|
| Account currency | Each account has one currency |
| Transaction currency | Inherit from account or override |
| Exchange rates | User-defined rates or API fetch |
| Conversion | Show amounts in account currency |
| Base currency | User sets preferred display currency |

### Supported Currencies (v1.0)
- USD, CNY, EUR, GBP, JPY, HKD, SGD, TWD, KRW, THB, ...
- Extensible list

---

## 10. Reports & Statistics

### Monthly Summary

| Metric | Description |
|--------|-------------|
| Total income | Sum of all income transactions |
| Total expense | Sum of all expense transactions |
| Net savings | Income - Expense |
| Top categories | Top 5 spending categories |
| Comparison | vs previous month |

### Category Breakdown

- Pie chart: Expense by category
- Bar chart: Income vs Expense by month

### Cash Flow

- Line chart: Balance trend over time
- Projection: Based on recurring transactions

### Budget Status

- Progress bars per category
- Over-budget warnings

### Export Formats

- PDF (for printing/sharing)
- Excel/CSV (for further analysis)

---

## 11. User Management

### User Properties

| Field | Type | Description |
|-------|------|-------------|
| username | String | Login name |
| email | String | Email address |
| nickname | String | Display name |
| password | String | Hashed password |
| defaultCurrency | String | Preferred currency |
| defaultAccountId | String | Default account for quick entry |

### Data Isolation

- Each user's data is isolated
- Users can share access to specific accounts
- Shared accounts visible to authorized team members

---

## 12. Authentication

| Feature | Included |
|---------|----------|
| Username/Password login | YES |
| Registration | YES |
| JWT session token | YES |
| Session refresh | YES |
| Password reset (email) | YES |

**Excluded for v1.0:**
- OAuth/OIDC (GitHub, Google, etc.)
- 2FA/TOTP
- API tokens

---

## 13. Technical Constraints

| Constraint | Value |
|------------|-------|
| Database | PostgreSQL 17+ |
| Amount precision | BIGINT (cents/fen) |
| Timestamps | Unix epoch seconds |
| Soft delete | Yes |
| Multi-tenancy | Single database, user isolation via user_id |

---

## 14. Out of Scope (Forever or Later)

- Bank reconciliation (automatic matching)
- Receipt scanning/OCR
- AI transaction categorization
- Mobile native app
- MCP protocol
- Cloud sync
- Multi-currency automated exchange rates

---

## 15. Open Questions

| # | Question | Answer |
|---|----------|--------|
| OQ1 | How to handle bank feed import? | A: Manual CSV import only for v1.0 |
| OQ2 | Should transfer between currencies convert automatically? | A: User confirms rate at transaction time |
| OQ3 | Team sharing model? | A: Each user has own data + shared accounts (future) |
| OQ4 | Mobile app or web-only? | A: Web (Nuxt) responsive for v1.0 |
| OQ5 | Team size? | A: ~20 members (estimate, not sure) |
| OQ6 | Bookkeeping workflow understanding? | A: Need guidance on standard bookkeeping workflows |
| OQ7 | Category design approach? | A: Need recommendations on expense/income categories |
| OQ8 | Report requirements? | A: Need help defining meaningful reports |
| OQ9 | Transaction entry preference? | A: Both quick entry + detailed form |
| OQ10 | Budget period? | A: Custom date (user-defined start day) |
| OQ11 | Data sharing? | A: Share selected accounts with team |
| OQ12 | Export formats? | A: PDF and Excel/CSV

---

## Appendix: Terms Glossary

| Term | Definition |
|------|------------|
| Account | A place to store money (bank account, wallet, credit card) |
| Transaction | Record of money movement in or out |
| Category | Classification of transaction type |
| Tag | Optional label for additional grouping |
| Budget | Spending limit per category |
| Reconciliation | Matching transactions with bank statement |
| Transfer | Moving money between accounts |
| Soft delete | Marking as deleted without removing from database |