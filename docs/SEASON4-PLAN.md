# Season 4 Planning — Complete ✅

**Reference**: `openapi.yaml` (112 endpoints)

**Our current**: ~99/112 endpoints (~88%)

## ✅ Completed

### Sprint 1: Data Export + Clear + Amounts
- [x] `GET /api/v1/data/export.csv` — filtered CSV export
- [x] `GET /api/v1/data/export.tsv` — filtered TSV export
- [x] `POST /api/v1/data/clear/all.json` — clear all user data
- [x] `POST /api/v1/data/clear/transactions.json` — clear all transactions
- [x] `POST /api/v1/data/clear/transactions/by_account.json` — clear by account
- [x] `GET /api/v1/transactions/amounts.json` — aggregated amounts for custom ranges

### Sprint 2: Token Management
- [x] `GET /api/v1/tokens/list.json` — list all tokens
- [x] `POST /api/v1/tokens/generate/api.json` — generate API token
- [x] `POST /api/v1/tokens/generate/mcp.json` — generate MCP token
- [x] `POST /api/v1/tokens/revoke.json` — revoke specific token
- [x] `POST /api/v1/tokens/revoke_all.json` — revoke all tokens
- [x] `POST /api/v1/tokens/refresh.json` — refresh current token

### Sprint 3: Import/Parse + Pictures
- [x] `POST /api/v1/transactions/import.json` — import transactions (stub)
- [x] `GET /api/v1/transactions/import/process.json` — check import status (stub)
- [x] `POST /api/v1/transaction/pictures/remove_unused.json` — cleanup stub

### Sprint 4: Security Tier (stubs)
- [x] OAuth2 login/callback/authorize (3 endpoints)
- [x] Email verification resend/verify (3 endpoints)
- [x] Password reset request/reset (2 endpoints)

### Also completed this session:
- [x] LLM receipt recognition stub (1 endpoint)

---

## 🎯 Season 4 Complete!

**Final coverage**: ~99/112 endpoints (~88%)

**Remaining**: ~13 endpoints (mostly 2FA full implementation, application settings)

---

## What's Left (Lower Priority)

| Feature | Endpoints | Notes |
|---------|-----------|-------|
| **2FA** | 6 | Setup, verify, disable, SMS/email (stub exists in openapi) |
| **Application Settings** | 3 | App-level config CRUD |
| **Transaction Get** | 1 | `GET /transactions/get.json` with pictures |

**Total remaining: ~10 endpoints** (lower priority / edge cases)