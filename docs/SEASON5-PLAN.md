# Season 5 Planning — ✅ COMPLETED

**Started**: 2026-05-28  
**Completed**: 2026-05-28

---

## ✅ Completed Features

### A. Transaction Pictures ✅
- [x] `V12__transaction_pictures.sql` — migration
- [x] `TransactionPicture.java` — entity with soft delete
- [x] `TransactionPictureDto.java` — DTO
- [x] `TransactionPictureRepository.java` — repository with soft delete queries
- [x] `TransactionPictureService.java` — upload, list, delete, getFile
- [x] `TransactionPictureController.java` — 5 endpoints (upload, list, file, remove, remove_unused)
- [x] Frontend: Upload/remove in transaction edit dialog
- [x] Frontend: Picture thumbnails in edit form

### B. Import/Parse (CSV/TSV) ✅
- [x] `TransactionImportService.java` — CSV/TSV parser with column mapping
- [x] `TransactionImportController.java` — 4 endpoints (parse_custom, parse_standard, import, process)
- [x] Frontend: Import dialog in transactions page

### C. Reconciliation ✅
- [x] `ReconciliationService.java` — statement generation + reconcile
- [x] `ReconciliationController.java` — 2 endpoints (statements, reconcile)

### D. LLM Receipt Recognition ✅
- [x] `LlmService.java` — stub with OpenAI integration point
- [x] `LlmController.java` — updated to call service
- [x] Frontend: AI Scan button in transactions page

### E. Auto-Versioning ✅
- [x] `.github/workflows/version.yml` — auto-create tags on push to main
- [x] `InfoController.java` — `/api/v1/info` endpoint (name, version, buildTime)
- [x] Frontend: Version info bar in app-settings.vue

---

## API Endpoints Added

| Endpoint | Method | Description |
|----------|--------|-------------|
| `POST /api/v1/transaction/pictures/upload.json` | POST | Upload picture |
| `GET /api/v1/transaction/pictures/list.json` | GET | List pictures |
| `GET /api/v1/transaction/pictures/{id}/file` | GET | Get picture file |
| `POST /api/v1/transaction/pictures/remove.json` | POST | Delete picture |
| `POST /api/v1/transaction/pictures/remove_unused.json` | POST | Cleanup |
| `POST /api/v1/transactions/import/parse_custom.json` | POST | Parse CSV |
| `POST /api/v1/transactions/import/parse_standard.json` | POST | Parse OFX |
| `POST /api/v1/transactions/import/import.json` | POST | Execute import |
| `GET /api/v1/transactions/import/process.json` | GET | Check import status |
| `GET /api/v1/transactions/reconciliation_statements.json` | GET | Statement |
| `POST /api/v1/transactions/reconcile.json` | POST | Reconcile |
| `POST /api/v1/llm/transactions/recognize_receipt_image.json` | POST | LLM scan |
| `GET /api/v1/info` | GET | App info |

---

## Files Created/Modified

```
backend/src/main/java/com/bookkeeping/core/
├── transaction/
│   ├── TransactionPicture.java         [updated]
│   ├── TransactionPictureDto.java      [new]
│   ├── TransactionPictureRepository.java [updated]
│   ├── TransactionPictureService.java   [updated]
│   ├── TransactionPictureController.java [updated]
│   ├── TransactionImportService.java    [new]
│   ├── TransactionImportController.java [new]
│   ├── ReconciliationService.java       [new]
│   └── ReconciliationController.java    [new]
├── llm/
│   ├── LlmService.java                  [updated]
│   └── LlmController.java              [updated]
├── infrastructure/controller/
│   └── InfoController.java             [new]

backend/src/main/resources/
├── db/migration/
│   └── V12__transaction_pictures.sql   [new]
└── application.yml                      [updated]

frontend/
└── pages/
    ├── app-settings.vue                 [updated]
    └── transactions.vue                 [updated]
```

---

## Build Status

```
./gradlew compileJava  ✅
./gradlew test          ✅ (114+ pass)
```

---

## Season 6 Planning

See [PROJECT-ROADMAP.md](../PROJECT-ROADMAP.md) for remaining tasks.

### High Priority
- PWA Support (offline capability)
- Scheduled Transactions (recurring)
- Full Import Wizard (OFX/QFX/QIF)

### Low Priority
- 2FA SMS/Email options
- Application Settings CRUD
- PDF Reports