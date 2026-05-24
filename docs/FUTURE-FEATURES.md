# Future Features — Feature Gaps vs ezBookkeeping

**Reference**: `openapi.yaml` (112 endpoints, 20 API tags) and `FRONTEND_PAGES.md` (30 pages, 2 UIs)

**Our current**: ~30 endpoints across 9 controllers

---

## API Coverage Map

### Auth (14 endpoints)
| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/api/authorize.json` | POST | Login with username/password | ✅ |
| `/api/register.json` | POST | Register new user | ✅ |
| `/api/logout.json` | GET | Logout / revoke token | ✅ |
| `/api/2fa/authorize.json` | POST | Complete 2FA login with TOTP | 🔴 Missing |
| `/api/2fa/recovery.json` | POST | 2FA login with recovery code | 🔴 Missing |
| `/api/oauth2/login` | GET | Initiate OAuth2 login | 🔴 Missing |
| `/api/oauth2/callback` | GET | OAuth2 provider callback | 🔴 Missing |
| `/api/oauth2/authorize.json` | POST | Finalize OAuth2 login | 🔴 Missing |
| `/api/verify_email/resend.json` | POST | Resend verification email | 🔴 Missing |
| `/api/verify_email/by_token.json` | POST | Verify email by token | 🔴 Missing |
| `/api/forget_password/request.json` | POST | Request password reset email | 🔴 Missing |
| `/api/forget_password/reset/by_token.json` | POST | Reset password by token | 🔴 Missing |

### Users & Profile (8 endpoints)
| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/api/v1/users/profile/get.json` | GET | Get full user profile | 🔴 Missing |
| `/api/v1/users/profile/update.json` | POST | Update profile (nickname, language, etc.) | 🔴 Missing |
| `/api/v1/users/avatar/update.json` | POST | Upload avatar image | 🔴 Missing |
| `/api/v1/users/avatar/remove.json` | POST | Remove avatar | 🔴 Missing |
| `/api/v1/users/verify_email/resend.json` | POST | Resend email verification (authed) | 🔴 Missing |
| `/api/v1/users/external_auth/list.json` | GET | List OAuth bindings | 🔴 Missing |
| `/api/v1/users/external_auth/unlink.json` | POST | Unlink OAuth provider | 🔴 Missing |

### Tokens (6 endpoints)
| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/api/v1/tokens/list.json` | GET | List all active tokens | 🔴 Missing |
| `/api/v1/tokens/generate/api.json` | POST | Generate API token | 🔴 Missing |
| `/api/v1/tokens/generate/mcp.json` | POST | Generate MCP token | 🔴 Missing |
| `/api/v1/tokens/revoke.json` | POST | Revoke specific token | 🔴 Missing |
| `/api/v1/tokens/revoke_all.json` | POST | Revoke all tokens | 🔴 Missing |
| `/api/v1/tokens/refresh.json` | POST | Refresh current JWT token | 🔴 Missing |

### Two-Factor Auth (6 endpoints)
| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/api/v1/users/2fa/status.json` | GET | Get 2FA status | 🔴 Missing |
| `/api/v1/users/2fa/enable/request.json` | POST | Request 2FA setup (get TOTP secret + QR) | 🔴 Missing |
| `/api/v1/users/2fa/enable/confirm.json` | POST | Confirm 2FA with TOTP passcode | 🔴 Missing |
| `/api/v1/users/2fa/disable.json` | POST | Disable 2FA | 🔴 Missing |
| `/api/v1/users/2fa/recovery/regenerate.json` | POST | Regenerate recovery codes | 🔴 Missing |

### Data Management (6 endpoints)
| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/api/v1/data/statistics.json` | GET | Get data statistics (counts) | 🔴 Missing |
| `/api/v1/data/clear/all.json` | POST | Clear all user data | 🔴 Missing |
| `/api/v1/data/clear/transactions.json` | POST | Clear all transactions | 🔴 Missing |
| `/api/v1/data/clear/transactions/by_account.json` | POST | Clear transactions by account | 🔴 Missing |
| `/api/v1/data/export.csv` | GET | Export all data as CSV | 🔴 Missing |
| `/api/v1/data/export.tsv` | GET | Export all data as TSV | 🔴 Missing |

### External Auth (OAuth2) (6 endpoints)
| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/oauth2/login` | GET | Redirect to OAuth2 provider | 🔴 Missing |
| `/oauth2/callback` | GET | OAuth2 callback | 🔴 Missing |
| `/api/oauth2/authorize.json` | POST | Finalize OAuth2 login | 🔴 Missing |
| `/api/v1/users/external_auth/list.json` | GET | List bound OAuth providers | 🔴 Missing |
| `/api/v1/users/external_auth/unlink.json` | POST | Unlink OAuth provider | 🔴 Missing |

### Transaction Tags (14 endpoints)
| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/api/v1/transaction/tags/list.json` | GET | List all tags | ✅ |
| `/api/v1/transaction/tags/get.json` | GET | Get single tag | ✅ |
| `/api/v1/transaction/tags/add.json` | POST | Create tag | ✅ |
| `/api/v1/transaction/tags/add_batch.json` | POST | Batch create tags | 🔴 Missing |
| `/api/v1/transaction/tags/modify.json` | POST | Update tag | ✅ |
| `/api/v1/transaction/tags/hide.json` | POST | Hide/unhide tag | 🔴 Missing |
| `/api/v1/transaction/tags/move.json` | POST | Reorder tags | 🔴 Missing |
| `/api/v1/transaction/tags/delete.json` | POST | Delete tag | ✅ |
| `/api/v1/transaction/tags/groups/list.json` | GET | List tag groups | 🔴 Missing |
| `/api/v1/transaction/tags/groups/get.json` | GET | Get tag group | 🔴 Missing |
| `/api/v1/transaction/tags/groups/add.json` | POST | Create tag group | 🔴 Missing |
| `/api/v1/transaction/tags/groups/modify.json` | POST | Update tag group | 🔴 Missing |
| `/api/v1/transaction/tags/groups/move.json` | POST | Reorder tag groups | 🔴 Missing |
| `/api/v1/transaction/tags/groups/delete.json` | POST | Delete tag group | 🔴 Missing |

### Transaction Categories (8 endpoints)
| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/api/v1/transaction/categories/list.json` | GET | List categories | ✅ |
| `/api/v1/transaction/categories/get.json` | GET | Get single category | ✅ |
| `/api/v1/transaction/categories/add.json` | POST | Create category | ✅ |
| `/api/v1/transaction/categories/add_batch.json` | POST | Batch create categories | 🔴 Missing |
| `/api/v1/transaction/categories/modify.json` | POST | Update category | ✅ |
| `/api/v1/transaction/categories/hide.json` | POST | Hide/unhide category | 🔴 Missing |
| `/api/v1/transaction/categories/move.json` | POST | Reorder categories | 🔴 Missing |
| `/api/v1/transaction/categories/delete.json` | POST | Delete category | ✅ |

### Accounts (10 endpoints)
| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/api/v1/accounts/list.json` | GET | List accounts | ✅ |
| `/api/v1/accounts/get.json` | GET | Get single account | ✅ |
| `/api/v1/accounts/add.json` | POST | Create account | ✅ |
| `/api/v1/accounts/modify.json` | POST | Update account | ✅ |
| `/api/v1/accounts/hide.json` | POST | Hide/unhide account | 🔴 Missing |
| `/api/v1/accounts/move.json` | POST | Reorder accounts | 🔴 Missing |
| `/api/v1/accounts/delete.json` | POST | Delete account (with sub-accounts) | 🔴 Missing |
| `/api/v1/accounts/sub_account/delete.json` | POST | Delete sub-account only | 🔴 Missing |
| `/api/v1/transactions/move/all.json` | POST | Move all transactions between accounts | 🔴 Missing |

### Transactions — Core (32 endpoints)

#### List & Query
| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/api/v1/transactions/list.json` | GET | List (paginated + cursor) | ✅ |
| `/api/v1/transactions/list/by_month.json` | GET | List by specific month | 🔴 Missing |
| `/api/v1/transactions/list/all.json` | GET | List all (unpaginated) | 🔴 Missing |
| `/api/v1/transactions/count.json` | GET | Count transactions | 🔴 Missing |
| `/api/v1/transactions/get.json` | GET | Get single transaction | ✅ |
| `/api/v1/transactions/reconciliation_statements.json` | GET | Get reconciliation statement | 🔴 Missing |

#### CRUD
| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/api/v1/transactions/add.json` | POST | Create transaction | ✅ |
| `/api/v1/transactions/modify.json` | POST | Update transaction | ✅ |
| `/api/v1/transactions/delete.json` | POST | Delete transaction | ✅ |

#### Batch Operations
| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/api/v1/transactions/batch_update/category.json` | POST | Batch update category | 🔴 Missing |
| `/api/v1/transactions/batch_update/account.json` | POST | Batch update account | 🔴 Missing |
| `/api/v1/transactions/batch_update/tag/add.json` | POST | Batch add tags | 🔴 Missing |
| `/api/v1/transactions/batch_update/tag/remove.json` | POST | Batch remove tags | 🔴 Missing |
| `/api/v1/transactions/batch_update/tag/clear.json` | POST | Batch clear tags | 🔴 Missing |
| `/api/v1/transactions/batch_delete.json` | POST | Batch delete | 🔴 Missing |

#### Statistics & Amounts
| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/api/v1/transactions/statistics.json` | GET | Category breakdown stats | ✅ |
| `/api/v1/transactions/statistics/trends.json` | GET | Monthly/yearly trends | 🔴 Missing |
| `/api/v1/transactions/statistics/asset_trends.json` | GET | Asset balance over time | 🔴 Missing |
| `/api/v1/transactions/amounts.json` | GET | Aggregated income/expense by range | 🔴 Missing |

#### Import/Export
| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/api/v1/transactions/parse_custom_file.json` | POST | Parse custom import file | 🔴 Missing |
| `/api/v1/transactions/parse_import.json` | POST | Parse standard import formats | 🔴 Missing |
| `/api/v1/transactions/import.json` | POST | Import transactions | 🔴 Missing |
| `/api/v1/transactions/import/process.json` | GET | Check async import progress | 🔴 Missing |

#### Move
| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/api/v1/transactions/move/all.json` | POST | Move all transactions between accounts | 🔴 Missing |

### Transaction Templates (8 endpoints)
| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/api/v1/transaction/templates/list.json` | GET | List templates | 🔴 Missing |
| `/api/v1/transaction/templates/get.json` | GET | Get single template | 🔴 Missing |
| `/api/v1/transaction/templates/add.json` | POST | Create template | 🔴 Missing |
| `/api/v1/transaction/templates/modify.json` | POST | Update template | 🔴 Missing |
| `/api/v1/transaction/templates/hide.json` | POST | Hide/unhide template | 🔴 Missing |
| `/api/v1/transaction/templates/move.json` | POST | Reorder templates | 🔴 Missing |
| `/api/v1/transaction/templates/delete.json` | POST | Delete template | 🔴 Missing |

### Transaction Pictures (2 endpoints)
| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/api/v1/transaction/pictures/upload.json` | POST | Upload picture attachment | 🔴 Missing |
| `/api/v1/transaction/pictures/remove_unused.json` | POST | Remove unused picture | 🔴 Missing |

### Insights Explorers (7 endpoints)
| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/api/v1/insights/explorers/list.json` | GET | List saved explorers | 🔴 Missing |
| `/api/v1/insights/explorers/get.json` | GET | Get explorer config | 🔴 Missing |
| `/api/v1/insights/explorers/add.json` | POST | Create explorer | 🔴 Missing |
| `/api/v1/insights/explorers/modify.json` | POST | Update explorer | 🔴 Missing |
| `/api/v1/insights/explorers/hide.json` | POST | Hide/show explorer | 🔴 Missing |
| `/api/v1/insights/explorers/move.json` | POST | Reorder explorers | 🔴 Missing |
| `/api/v1/insights/explorers/delete.json` | POST | Delete explorer | 🔴 Missing |

### Exchange Rates (3 endpoints)
| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/api/v1/exchange_rates/latest.json` | GET | Get latest exchange rates | 🔴 Missing |
| `/api/v1/exchange_rates/user_custom/update.json` | POST | Set custom exchange rate | 🔴 Missing |
| `/api/v1/exchange_rates/user_custom/delete.json` | POST | Delete custom rate | 🔴 Missing |

### Application Cloud Settings (3 endpoints)
| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/api/v1/users/settings/cloud/get.json` | GET | Get cloud settings | 🔴 Missing |
| `/api/v1/users/settings/cloud/update.json` | POST | Update cloud settings | 🔴 Missing |
| `/api/v1/users/settings/cloud/disable.json` | POST | Disable cloud sync | 🔴 Missing |

### LLM (1 endpoint)
| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/api/v1/llm/transactions/recognize_receipt_image.json` | POST | AI receipt image recognition | 🔴 Missing |

### MCP (1 endpoint)
| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/mcp` | POST | MCP JSON-RPC endpoint | 🔴 Missing |

### System (3 endpoints)
| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/healthz.json` | GET | Health check | ✅ |
| `/server_settings.js` | GET | Server settings as JS | 🔴 Missing |
| `/api/systems/version.json` | GET | Get server version | 🔴 Missing |

---

## Complete Feature List (All Missing Items)

### Authentication & Security
| # | Feature | Endpoints | Complexity |
|---|---------|-----------|------------|
| F-A1 | 2FA (TOTP) — enable, confirm, disable, recovery codes | 6 | High |
| F-A2 | OAuth2 / OIDC login (GitHub, Google, etc.) | 6 | High |
| F-A3 | Password reset (forget, reset by token) | 2 | Medium |
| F-A4 | Email verification (resend, verify by token) | 2 | Medium |
| F-A5 | Token management (list, generate API/MCP, revoke, refresh) | 6 | Medium |
| F-A6 | Session management (device list, revoke individual/bulk) | via tokens | Low |

### User Profile & Settings
| # | Feature | Endpoints | Complexity |
|---|---------|-----------|------------|
| F-U1 | Extended profile (avatar upload/remove, nickname, language, currency, week start, date format) | 5 | Medium |
| F-U2 | External auth binding (list/unlink OAuth providers) | 2 | Medium |
| F-U3 | Application cloud settings sync | 3 | Medium |
| F-U4 | User data statistics (counts of all entities) | 1 | Low |

### Data Management
| # | Feature | Endpoints | Complexity |
|---|---------|-----------|------------|
| F-D1 | Data export (CSV, TSV with filters) | 2 | Medium |
| F-D2 | Data clear (all data, all transactions, by account) | 3 | Medium |
| F-D3 | Scheduled transactions (cron-based auto-creation) | 0 (backend only) | High |

### Accounts
| # | Feature | Endpoints | Complexity |
|---|---------|-----------|------------|
| F-AC1 | Sub-accounts (two-level: parent + children) | via list/get | Medium |
| F-AC2 | Hide/unhide accounts | 1 | Low |
| F-AC3 | Reorder accounts (drag-to-sort) | 1 | Low |
| F-AC4 | Delete account with sub-accounts / sub-account only | 2 | Medium |
| F-AC5 | Move all transactions between accounts | 1 | Medium |

### Transactions
| # | Feature | Endpoints | Complexity |
|---|---------|-----------|------------|
| F-T1 | Cursor-based pagination (max_time, count) | in list | Low |
| F-T2 | List by month (year + month params) | 1 | Low |
| F-T3 | List all transactions (unpaginated, for export) | 1 | Low |
| F-T4 | Transaction count (with filters) | 1 | Low |
| F-T5 | Batch operations (update category, account, tags, delete) | 7 | Medium |
| F-T6 | Custom time range amounts (income/expense aggregation) | 1 | Medium |
| F-T7 | Reconciliation statement (account + date range statement) | 1 | Medium |
| F-T8 | Import transactions (CSV, OFX, QFX, QIF, custom parsing) | 4 | High |
| F-T9 | Transaction pictures (receipt attachments) | 2 | Medium |
| F-T10 | Scheduled transactions (template with repeat config) | 0 (backend only) | High |

### Categories
| # | Feature | Endpoints | Complexity |
|---|---------|-----------|------------|
| F-C1 | Three-level categories (parent + children, parent_id) | via list | Medium |
| F-C2 | Batch create categories | 1 | Low |
| F-C3 | Hide/unhide categories | 1 | Low |
| F-C4 | Reorder categories (drag-to-sort) | 1 | Low |
| F-C5 | Preset category templates (batch add income/expense) | via batch | Low |

### Tags
| # | Feature | Endpoints | Complexity |
|---|---------|-----------|------------|
| F-TG1 | Tag groups (group tags for filtering) | 5 | Medium |
| F-TG2 | Batch create tags | 1 | Low |
| F-TG3 | Hide/unhide tags | 1 | Low |
| F-TG4 | Reorder tags (drag-to-sort) | 1 | Low |

### Templates
| # | Feature | Endpoints | Complexity |
|---|---------|-----------|------------|
| F-TM1 | Transaction templates CRUD (save as template, create from template) | 7 | Medium |

### Statistics & Insights
| # | Feature | Endpoints | Complexity |
|---|---------|-----------|------------|
| F-S1 | Trend analysis (monthly/yearly income/expense trends) | 1 | Medium |
| F-S2 | Asset trends (balance over time per account) | 1 | Medium |
| F-S3 | Insights explorer (saved custom query + chart configs) | 7 | High |

### Exchange Rates & Multi-Currency
| # | Feature | Endpoints | Complexity |
|---|---------|-----------|------------|
| F-ER1 | Exchange rates (latest rates from provider) | 1 | Medium |
| F-ER2 | Custom exchange rates (user-defined rates) | 2 | Low |

### AI / LLM
| # | Feature | Endpoints | Complexity |
|---|---------|-----------|------------|
| F-AI1 | Receipt image recognition (LLM-powered parsing) | 1 | High |
| F-AI2 | MCP (Model Context Protocol) server | 1 | High |

### System
| # | Feature | Endpoints | Complexity |
|---|---------|-----------|------------|
| F-SY1 | Server settings endpoint (JS file for frontend) | 1 | Low |
| F-SY2 | Server version endpoint | ✅ | — |

---

## Feature Count Summary

| Category | Missing Features | Total Endpoints |
|----------|-----------------|-----------------|
| Authentication & Security | 6 | 22 |
| User Profile & Settings | 4 | 11 |
| Data Management | 3 | 6 |
| Accounts | 5 | 6 |
| Transactions | 10 | 23 |
| Categories | 5 | 5 |
| Tags | 4 | 8 |
| Templates | 1 | 7 |
| Statistics & Insights | 3 | 9 |
| Exchange Rates | 2 | 3 |
| AI / LLM | 2 | 2 |
| System | 1 | 1 |
| **Total** | **~46 features** | **~103 endpoints** |

> Note: 112 total endpoints minus ~30 we already have = ~82 missing. Some features (like batch ops) share 1 endpoint but enable many UI behaviors. Counted by feature for clarity.

---

## Roadmap (Unordered — for Planning)

### Tier 1 — Essential Completeness
- F-AC2/3/4/5: Account hide, reorder, delete, move transactions
- F-T1/2/3/4: Cursor pagination, list by month, list all, count
- F-T5: Batch transaction operations
- F-C2/3/4: Batch category create, hide, reorder
- F-TG2/3/4: Batch tag create, hide, reorder
- F-D1/2: Data export CSV/TSV, data clear
- F-U1: Extended user profile (avatar, language, currency)

### Tier 2 — Feature Richness
- F-TG1: Tag groups
- F-TM1: Transaction templates
- F-T6: Custom time range amounts
- F-T7: Reconciliation statement
- F-AC1: Sub-accounts
- F-D3: Scheduled transactions (backend cron)
- F-ER1/2: Exchange rates + custom rates
- F-C1: Three-level category hierarchy

### Tier 3 — Advanced Analytics
- F-S1/2: Trend analysis + asset trends
- F-S3: Insights explorers
- F-T8: Import/parse transactions
- F-T9: Transaction pictures

### Tier 4 — Security & Auth
- F-A1: 2FA (TOTP)
- F-A2: OAuth2/OIDC login
- F-A3/4: Password reset + email verification
- F-A5: Token management
- F-U2/3: OAuth binding + cloud settings sync

### Tier 5 — AI / Experimental
- F-AI1: LLM receipt recognition
- F-AI2: MCP server