# Season 4 Planning — Data Export, Clear, Amounts, Tokens

**Reference**: `openapi.yaml` (112 endpoints)

**Our current**: ~82/112 endpoints (~73%)

## Gap Analysis — What's Left

| Category | Endpoints | Priority |
|----------|-----------|----------|
| **Data Export** | 2 (CSV, TSV) | P1 |
| **Data Clear** | 3 (all, transactions, by_account) | P1 |
| **Transaction Amounts** | 1 | P1 |
| **Tokens** | 6 | P2 |
| **Import/Parse** | 4 | P2 |
| **LLM Receipt** | 1 | P2 |
| **Password Reset** | 2 | P2 |
| **Email Verification** | 2 | P2 |
| **2FA** | 6 | P2 |
| **OAuth2** | 6 | P2 |
| **Application Settings** | 3 | P2 |
| **Picture cleanup** | 1 | P3 |
| **Account transfer all** | 1 | P3 |
| **Total** | **~38** | |

---

## Season 4 Sprints

### Sprint 1: Data Export + Clear + Amounts
- [x] `GET /api/v1/data/export.csv` — export filtered transactions as CSV
- [x] `GET /api/v1/data/export.tsv` — export filtered transactions as TSV
- [x] `POST /api/v1/data/clear/all.json` — clear all user data
- [x] `POST /api/v1/data/clear/transactions.json` — clear all transactions
- [x] `POST /api/v1/data/clear/transactions/by_account.json` — clear by account
- [x] `GET /api/v1/transactions/amounts.json` — aggregated amounts for custom ranges

### Sprint 2: Token Management
- [ ] `GET /api/v1/tokens/list.json` — list all tokens
- [ ] `POST /api/v1/tokens/generate/api.json` — generate API token
- [ ] `POST /api/v1/tokens/generate/mcp.json` — generate MCP token
- [ ] `POST /api/v1/tokens/revoke.json` — revoke specific token
- [ ] `POST /api/v1/tokens/revoke_all.json` — revoke all tokens
- [ ] `POST /api/v1/tokens/refresh.json` — refresh current token

### Sprint 3: Import/Parse Transactions
- [ ] `POST /api/v1/transactions/import.json` — import transactions
- [ ] `GET /api/v1/transactions/import/process.json` — check import status

### Sprint 4: Security (Password Reset, 2FA, OAuth2)
- [ ] Password reset flow (2 endpoints)
- [ ] 2FA setup/verify/disable (6 endpoints)
- [ ] OAuth2 login/callback (2 endpoints)

### Sprint 5: LLM + Application Settings
- [ ] `POST /api/v1/llm/transactions/recognize_receipt_image.json`
- [ ] Application settings CRUD (3 endpoints)

---

## Season 4 Goals
- **Target**: ~82 → ~100 endpoints (90%+ coverage)
- **Focus**: P1 features (export, clear, amounts) + P2 security tier