# Bookkeeping Project — Season Overview

> Last updated: 2026-05-28

---

## Season Status Overview

| Season | Status | Coverage | Notes |
|--------|--------|----------|-------|
| **Season 1** | ✅ Complete | Core features | Basic implementation |
| **Season 2** | ✅ Complete | 9 controllers, 30 endpoints | User, Auth, Account, Transaction, Category, Tag, Budget |
| **Season 3** | ✅ Complete | ~82 endpoints added | Advanced features (Data Management, Batch Ops, Templates) |
| **Season 4** | ✅ Complete | ~99/112 endpoints (88%) | Data Export, Tokens, OAuth, Email, Password Reset, LLM |
| **Season 5** | 🚧 In Progress | 11 endpoints added | Transaction Pictures, Import, Reconciliation, Auto-version |
| **Season 6+** | 📋 Planned | ~13 remaining | See below |

---

## Current: Season 5 🚧

### ✅ Completed (2026-05-28)

| Feature | Endpoints | Status |
|---------|-----------|--------|
| **Transaction Pictures** | upload, list, remove, remove_unused (4) | ✅ |
| **CSV/TSV Import** | parse_custom, parse_standard, import, process (4) | ✅ |
| **Reconciliation** | statements, reconcile (2) | ✅ |
| **LLM Receipt** | recognize_receipt_image (1) | ✅ Stub |
| **Auto-Versioning** | CI creates tags vYYMM.P on push | ✅ |

### Files Created in Season 5

```
backend/src/main/java/com/bookkeeping/core/transaction/
├── TransactionPictureDto.java     [new]
├── TransactionPictureService.java  [new]
├── TransactionImportService.java   [new]
├── TransactionImportController.java [new]
├── ReconciliationService.java     [new]
└── ReconciliationController.java   [new]

backend/src/main/resources/db/migration/
└── V12__transaction_pictures.sql  [new]
```

### Frontend Updates
- `pages/app-settings.vue` — Version info bar added

---

## Remaining: Season 6+ 📋

### OpenAPI Coverage Gap (~13 endpoints)

| Category | Endpoint | Priority |
|----------|----------|----------|
| **2FA** | Enable/disable/request (3) | Medium |
| **2FA** | SMS/Email options (3) | Low |
| **Application Settings** | CRUD endpoints (3) | Low |
| **Transaction** | Get with pictures (1) | Medium |
| **Exchange Rates** | Already done (3) | ✅ |

### New Features (Not in OpenAPI)

| Feature | Description | Effort |
|---------|-------------|--------|
| **MCP Server** | Model Context Protocol for AI agents | High |
| **Multi-currency** | Full exchange rate integration | Medium |
| **Scheduled Transactions** | Recurring transactions (cron) | High |
| **PWA Support** | Progressive Web App for offline | Medium |
| **Mobile App** | Native iOS/Android via Capacitor | High |
| **Reports PDF Export** | Generate PDF reports | Medium |
| **Data Import Wizard** | Full OFX/QFX/QIF support | Medium |
| **Backup/Restore** | Full data backup to cloud | Medium |

---

## OpenAPI Endpoint Coverage

```
Total: 112 endpoints
├── ✅ Implemented: ~104 (93%)
├── 🔄 In Progress: 0
└── 📋 Remaining: ~8 (low priority)
```

### Remaining ~8 Endpoints (Low Priority)

| Endpoint | Description | Notes |
|----------|-------------|-------|
| `POST /api/v1/users/2fa/request.json` | Request 2FA setup | TOTP exists, request flow pending |
| `POST /api/v1/users/2fa/verify.json` | Verify 2FA code | TOTP exists |
| `POST /api/v1/users/2fa/disable.json` | Disable 2FA | TOTP exists |
| `POST /api/v1/users/2fa/sms_enable.json` | Enable SMS 2FA | Stub |
| `POST /api/v1/users/2fa/email_enable.json` | Enable Email 2FA | Stub |
| `POST /api/v1/users/2fa/sms_verify.json` | Verify SMS code | Stub |
| `POST /api/v1/users/2fa/email_verify.json` | Verify Email code | Stub |
| `GET /api/v1/transactions/get.json` | Get transaction with pictures | Not started |

---

## Recommended Next Steps

### Option A: Finish OpenAPI (Quick)
1. Implement remaining 2FA flow
2. Add Application Settings CRUD
3. Add Transaction Get with pictures

### Option B: New Features (High Value)
1. **Scheduled Transactions** — recurring transactions
2. **PWA Support** — offline capability
3. **Full Import Wizard** — OFX/QFX/QIF parsing

### Option C: Polish & Stabilize
1. Add frontend for Transaction Pictures
2. Add frontend for Import Wizard
3. Fix remaining test failures
4. Update documentation

---

## CI/CD Pipeline

```
Push to main
    ↓
version.yml → Creates tag v2605.X
    ↓
ci.yml → Builds & Tests
docker.yml → Docker image → ghcr.io
```

### Current Version
- **Latest Tag**: Created by version.yml on push
- **Format**: v{YY}{MM}.{patch} (e.g., v2605.2)

---

## Documentation

| File | Description |
|------|-------------|
| `AGENTS.md` | Development guide, tech stack, patterns |
| `docs/USER-GUIDE.md` | API documentation |
| `docs/ADMIN-GUIDE.md` | Deployment, configuration |
| `docs/SEASON*-PLAN.md` | Season planning documents |
| `openapi.yaml` | Complete API specification |

---

## Statistics

| Metric | Value |
|--------|-------|
| **Total Endpoints** | 112 |
| **Implemented** | ~104 (93%) |
| **Java Files** | ~120 |
| **Frontend Pages** | 30+ |
| **Migrations** | 12 |
| **Test Coverage** | 114+ tests passing |
| **Seasons Completed** | 5 |