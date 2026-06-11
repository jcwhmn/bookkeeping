# API Endpoint Inventory

> Generated: 2026-06-01

## Summary

| Category | Count | Description |
|----------|-------|-------------|
| **System** | 3 | Public health/version endpoints |
| **Auth** | 12 | Authentication, OAuth2, password reset |
| **User** | 15 | Profile, avatar, 2FA, external auth |
| **Tokens** | 6 | API/MCP token management |
| **Accounts** | 8 | Account CRUD |
| **Transactions** | 24 | Transaction CRUD, stats, import |
| **Categories** | 8 | Category CRUD |
| **Tags** | 14 | Tag and TagGroup CRUD |
| **Templates** | 7 | Transaction template CRUD |
| **Insights** | 7 | Insights explorer CRUD |
| **LLM** | 1 | Receipt image recognition |
| **Exchange Rates** | 3 | Exchange rate management |
| **MCP** | 1 | Model Context Protocol |
| **Data Management** | 6 | Export, clear statistics |
| **Pictures** | 2 | Transaction picture upload |
| **TOTAL** | **117** | |

---

## 1. System Endpoints (3) - PUBLIC

| # | Method | Endpoint | Auth | Description |
|---|--------|----------|------|-------------|
| 1 | GET | `/healthz.json` | No | Health check |
| 2 | GET | `/server_settings.js` | No | Server settings as JS |
| 3 | GET | `/api/systems/version.json` | No | Version info |

---

## 2. Auth Endpoints (12) - MIXED (Some Public)

### 2.1 Public Authentication

| # | Method | Endpoint | Auth | Description |
|---|--------|----------|------|-------------|
| 4 | POST | `/api/authorize.json` | No | Login |
| 5 | POST | `/api/register.json` | No | Register |
| 6 | GET | `/api/logout.json` | Yes | Logout |

### 2.2 2FA

| # | Method | Endpoint | Auth | Description |
|---|--------|----------|------|-------------|
| 7 | POST | `/api/2fa/authorize.json` | No* | 2FA login complete |
| 8 | POST | `/api/2fa/recovery.json` | No* | 2FA recovery code login |

### 2.3 OAuth2

| # | Method | Endpoint | Auth | Description |
|---|--------|----------|------|-------------|
| 9 | GET | `/oauth2/login` | No | Initiate OAuth login |
| 10 | GET | `/oauth2/callback` | No | OAuth callback |
| 11 | POST | `/api/oauth2/authorize.json` | No* | Finalize OAuth login |

### 2.4 Email Verification

| # | Method | Endpoint | Auth | Description |
|---|--------|----------|------|-------------|
| 12 | POST | `/api/verify_email/resend.json` | No | Resend verification |
| 13 | POST | `/api/verify_email/by_token.json` | No | Verify email |

### 2.5 Password Reset

| # | Method | Endpoint | Auth | Description |
|---|--------|----------|------|-------------|
| 14 | POST | `/api/forget_password/request.json` | No | Request reset |
| 15 | POST | `/api/forget_password/reset/by_token.json` | No | Reset with token |

---

## 3. User Endpoints (15) - AUTHENTICATED

| # | Method | Endpoint | Description |
|---|--------|----------|-------------|
| 16 | GET | `/api/v1/users/profile/get.json` | Get profile |
| 17 | POST | `/api/v1/users/profile/update.json` | Update profile |
| 18 | POST | `/api/v1/users/avatar/update.json` | Upload avatar |
| 19 | POST | `/api/v1/users/avatar/remove.json` | Remove avatar |
| 20 | POST | `/api/v1/users/verify_email/resend.json` | Resend email verification |
| 21 | GET | `/api/v1/users/external_auth/list.json` | List external auth |
| 22 | POST | `/api/v1/users/external_auth/unlink.json` | Unlink external auth |
| 23 | GET | `/api/v1/users/settings/cloud/get.json` | Get cloud settings |
| 24 | POST | `/api/v1/users/settings/cloud/update.json` | Update cloud settings |
| 25 | POST | `/api/v1/users/settings/cloud/disable.json` | Disable cloud sync |
| 26 | GET | `/api/v1/users/2fa/status.json` | Get 2FA status |
| 27 | POST | `/api/v1/users/2fa/enable/request.json` | Request 2FA setup |
| 28 | POST | `/api/v1/users/2fa/enable/confirm.json` | Confirm 2FA setup |
| 29 | POST | `/api/v1/users/2fa/disable.json` | Disable 2FA |
| 30 | POST | `/api/v1/users/2fa/recovery/regenerate.json` | Regenerate recovery codes |

---

## 4. Token Endpoints (6) - AUTHENTICATED

| # | Method | Endpoint | Description |
|---|--------|----------|-------------|
| 31 | GET | `/api/v1/tokens/list.json` | List tokens |
| 32 | POST | `/api/v1/tokens/generate/api.json` | Generate API token |
| 33 | POST | `/api/v1/tokens/generate/mcp.json` | Generate MCP token |
| 34 | POST | `/api/v1/tokens/revoke.json` | Revoke specific token |
| 35 | POST | `/api/v1/tokens/revoke_all.json` | Revoke all tokens |
| 36 | POST | `/api/v1/tokens/refresh.json` | Refresh token |

---

## 5. Account Endpoints (8) - AUTHENTICATED

| # | Method | Endpoint | Description |
|---|--------|----------|-------------|
| 37 | GET | `/api/v1/accounts/list.json` | List accounts |
| 38 | GET | `/api/v1/accounts/get.json` | Get single account |
| 39 | POST | `/api/v1/accounts/add.json` | Create account |
| 40 | POST | `/api/v1/accounts/modify.json` | Update account |
| 41 | POST | `/api/v1/accounts/hide.json` | Hide/unhide account |
| 42 | POST | `/api/v1/accounts/move.json` | Reorder accounts |
| 43 | POST | `/api/v1/accounts/delete.json` | Delete account |
| 44 | POST | `/api/v1/accounts/sub_account/delete.json` | Delete sub-account |

---

## 6. Transaction Endpoints (24) - AUTHENTICATED

### 6.1 Read

| # | Method | Endpoint | Description |
|---|--------|----------|-------------|
| 45 | GET | `/api/v1/transactions/count.json` | Count transactions |
| 46 | GET | `/api/v1/transactions/list.json` | List transactions (paginated) |
| 47 | GET | `/api/v1/transactions/list/by_month.json` | List by month |
| 48 | GET | `/api/v1/transactions/list/all.json` | List all (time range) |
| 49 | GET | `/api/v1/transactions/get.json` | Get single transaction |
| 50 | GET | `/api/v1/transactions/reconciliation_statements.json` | Reconciliation statement |
| 51 | GET | `/api/v1/transactions/statistics.json` | Get statistics |
| 52 | GET | `/api/v1/transactions/statistics/trends.json` | Statistics trends |
| 53 | GET | `/api/v1/transactions/statistics/asset_trends.json` | Asset trends |
| 54 | GET | `/api/v1/transactions/amounts.json` | Aggregated amounts |

### 6.2 Write

| # | Method | Endpoint | Description |
|---|--------|----------|-------------|
| 55 | POST | `/api/v1/transactions/add.json` | Create transaction |
| 56 | POST | `/api/v1/transactions/modify.json` | Update transaction |
| 57 | POST | `/api/v1/transactions/delete.json` | Delete transaction |

### 6.3 Batch Operations

| # | Method | Endpoint | Description |
|---|--------|----------|-------------|
| 58 | POST | `/api/v1/transactions/batch_update/category.json` | Batch update category |
| 59 | POST | `/api/v1/transactions/batch_update/account.json` | Batch update account |
| 60 | POST | `/api/v1/transactions/batch_update/tag/add.json` | Batch add tags |
| 61 | POST | `/api/v1/transactions/batch_update/tag/remove.json` | Batch remove tags |
| 62 | POST | `/api/v1/transactions/batch_update/tag/clear.json` | Batch clear tags |
| 63 | POST | `/api/v1/transactions/move/all.json` | Move transactions |
| 64 | POST | `/api/v1/transactions/batch_delete.json` | Batch delete |
| 65 | POST | `/api/v1/transactions/reconcile.json` | Reconcile |

### 6.4 Import

| # | Method | Endpoint | Description |
|---|--------|----------|-------------|
| 66 | POST | `/api/v1/transactions/parse_custom_file.json` | Parse custom file |
| 67 | POST | `/api/v1/transactions/parse_import.json` | Parse standard file |
| 68 | POST | `/api/v1/transactions/import.json` | Execute import |

---

## 7. Category Endpoints (8) - AUTHENTICATED

| # | Method | Endpoint | Description |
|---|--------|----------|-------------|
| 69 | GET | `/api/v1/transaction/categories/list.json` | List categories |
| 70 | GET | `/api/v1/transaction/categories/get.json` | Get category |
| 71 | POST | `/api/v1/transaction/categories/add.json` | Create category |
| 72 | POST | `/api/v1/transaction/categories/add_batch.json` | Batch create |
| 73 | POST | `/api/v1/transaction/categories/modify.json` | Update category |
| 74 | POST | `/api/v1/transaction/categories/hide.json` | Hide/unhide |
| 75 | POST | `/api/v1/transaction/categories/move.json` | Reorder |
| 76 | POST | `/api/v1/transaction/categories/delete.json` | Delete category |

---

## 8. Tag Endpoints (14) - AUTHENTICATED

### 8.1 Tag Groups

| # | Method | Endpoint | Description |
|---|--------|----------|-------------|
| 77 | GET | `/api/v1/transaction/tags/groups/list.json` | List groups |
| 78 | GET | `/api/v1/transaction/tags/groups/get.json` | Get group |
| 79 | POST | `/api/v1/transaction/tags/groups/add.json` | Create group |
| 80 | POST | `/api/v1/transaction/tags/groups/modify.json` | Update group |
| 81 | POST | `/api/v1/transaction/tags/groups/move.json` | Reorder groups |
| 82 | POST | `/api/v1/transaction/tags/groups/delete.json` | Delete group |

### 8.2 Tags

| # | Method | Endpoint | Description |
|---|--------|----------|-------------|
| 83 | GET | `/api/v1/transaction/tags/list.json` | List tags |
| 84 | GET | `/api/v1/transaction/tags/get.json` | Get tag |
| 85 | POST | `/api/v1/transaction/tags/add.json` | Create tag |
| 86 | POST | `/api/v1/transaction/tags/add_batch.json` | Batch create |
| 87 | POST | `/api/v1/transaction/tags/modify.json` | Update tag |
| 88 | POST | `/api/v1/transaction/tags/hide.json` | Hide/unhide |
| 89 | POST | `/api/v1/transaction/tags/move.json` | Reorder |
| 90 | POST | `/api/v1/transaction/tags/delete.json` | Delete tag |

---

## 9. Template Endpoints (7) - AUTHENTICATED

| # | Method | Endpoint | Description |
|---|--------|----------|-------------|
| 91 | GET | `/api/v1/transaction/templates/list.json` | List templates |
| 92 | GET | `/api/v1/transaction/templates/get.json` | Get template |
| 93 | POST | `/api/v1/transaction/templates/add.json` | Create template |
| 94 | POST | `/api/v1/transaction/templates/modify.json` | Update template |
| 95 | POST | `/api/v1/transaction/templates/hide.json` | Hide/unhide |
| 96 | POST | `/api/v1/transaction/templates/move.json` | Reorder |
| 97 | POST | `/api/v1/transaction/templates/delete.json` | Delete template |

---

## 10. Insights Endpoints (7) - AUTHENTICATED

| # | Method | Endpoint | Description |
|---|--------|----------|-------------|
| 98 | GET | `/api/v1/insights/explorers/list.json` | List explorers |
| 99 | GET | `/api/v1/insights/explorers/get.json` | Get explorer |
| 100 | POST | `/api/v1/insights/explorers/add.json` | Create explorer |
| 101 | POST | `/api/v1/insights/explorers/modify.json` | Update explorer |
| 102 | POST | `/api/v1/insights/explorers/hide.json` | Hide/unhide |
| 103 | POST | `/api/v1/insights/explorers/move.json` | Reorder |
| 104 | POST | `/api/v1/insights/explorers/delete.json` | Delete explorer |

---

## 11. LLM Endpoint (1) - AUTHENTICATED

| # | Method | Endpoint | Description |
|---|--------|----------|-------------|
| 105 | POST | `/api/v1/llm/transactions/recognize_receipt_image.json` | AI receipt scan |

---

## 12. Exchange Rate Endpoints (3) - AUTHENTICATED

| # | Method | Endpoint | Description |
|---|--------|----------|-------------|
| 106 | GET | `/api/v1/exchange_rates/latest.json` | Get latest rates |
| 107 | POST | `/api/v1/exchange_rates/user_custom/update.json` | Update custom rate |
| 108 | POST | `/api/v1/exchange_rates/user_custom/delete.json` | Delete custom rate |

---

## 13. MCP Endpoint (1) - AUTHENTICATED

| # | Method | Endpoint | Description |
|---|--------|----------|-------------|
| 109 | POST | `/mcp` | MCP JSON-RPC endpoint |

---

## 14. Data Management Endpoints (6) - AUTHENTICATED

| # | Method | Endpoint | Description |
|---|--------|----------|-------------|
| 110 | GET | `/api/v1/data/statistics.json` | Get data statistics |
| 111 | POST | `/api/v1/data/clear/all.json` | Clear all data |
| 112 | POST | `/api/v1/data/clear/transactions.json` | Clear transactions |
| 113 | POST | `/api/v1/data/clear/transactions/by_account.json` | Clear by account |
| 114 | GET | `/api/v1/data/export.csv` | Export CSV |
| 115 | GET | `/api/v1/data/export.tsv` | Export TSV |

---

## 15. Picture Endpoints (2) - AUTHENTICATED

| # | Method | Endpoint | Description |
|---|--------|----------|-------------|
| 116 | POST | `/api/v1/transaction/pictures/upload.json` | Upload picture |
| 117 | POST | `/api/v1/transaction/pictures/remove_unused.json` | Remove unused |

---

## 16. Scheduled Transaction Endpoints (7) - AUTHENTICATED (Not in OpenAPI)

| # | Method | Endpoint | Description |
|---|--------|----------|-------------|
| 118 | POST | `/api/v1/scheduled_transactions/add.json` | Create |
| 119 | GET | `/api/v1/scheduled_transactions/list.json` | List all |
| 120 | GET | `/api/v1/scheduled_transactions/get.json` | Get one |
| 121 | POST | `/api/v1/scheduled_transactions/modify.json` | Update |
| 122 | POST | `/api/v1/scheduled_transactions/delete.json` | Delete |
| 123 | POST | `/api/v1/scheduled_transactions/toggle_active.json` | Enable/Disable |
| 124 | GET | `/api/v1/scheduled_transactions/statistics.json` | Stats |