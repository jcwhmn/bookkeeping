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
| Primary | Small team (2-5 members) |
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

---

## 4. Transaction Types

| Type | Direction | Examples |
|------|-----------|----------|
| Income | + | Salary, bonus, refund |
| Expense | - | Groceries, transport, utilities |
| Transfer | 0 | Move money between accounts |
| Modify Balance | ± | Correction, interest |

---

## 5. Category System

### Default Categories (Pre-seeded)

**Income:**
- Salary, Bonus, Investment, Refund, Gift, Other

**Expense:**
- Food (Groceries, Restaurants), Transport, Housing, Utilities, Healthcare, Entertainment, Shopping, Education, Insurance, Taxes, Other

---

## 6. Tag System

Tags are optional labels for transactions.

**Use Cases:**
- Mark business vs personal expenses
- Track tax-deductible items (#tax-deductible)
- Flag pending reimbursement (#reimburse-to-client)

---

## 7. Transaction Templates

For recurring transactions:
- Daily, Weekly, Monthly, Yearly schedules
- Custom interval (every N days)

---

## 8. Budget Management

Per-category spending limits:
- Monthly, Weekly, Yearly periods
- Alert when threshold reached (default: 80%)

---

## 9. Multi-Currency Support

- Each account has one currency
- User-defined exchange rates
- Base currency for display

### Supported Currencies (v1.0)
- USD, CNY, EUR, GBP, JPY, HKD, SGD, TWD, KRW, THB

---

## 10. Reports & Statistics

- Monthly Summary (income, expense, net savings)
- Category Breakdown (pie/bar charts)
- Cash Flow trend
- Budget Status progress bars

---

## 11. Open Questions

| # | Question | Options |
|---|----------|---------|
| OQ1 | How to handle bank feed import? | Manual CSV only for v1.0 |
| OQ2 | Should transfer between currencies convert automatically? | User confirms rate at transaction time |
| OQ3 | Team sharing model? | Each user has own data + shared accounts (future) |
| OQ4 | Mobile app or web-only? | Web (Nuxt) responsive for v1.0 |

---

## Appendix: Terms Glossary

| Term | Definition |
|------|------------|
| Account | A place to store money (bank account, wallet, credit card) |
| Transaction | Record of money movement in or out |
| Category | Classification of transaction type |
| Tag | Optional label for additional grouping |
| Budget | Spending limit per category |
| Transfer | Moving money between accounts |
| Soft delete | Marking as deleted without removing from database |
