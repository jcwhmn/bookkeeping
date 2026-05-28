# Season 5 Planning — ✅ IN PROGRESS

**Started**: 2026-05-28

---

## ✅ Completed

### A. Transaction Pictures
- [x] `V12__transaction_pictures.sql` — migration
- [x] `TransactionPicture.java` — entity with soft delete
- [x] `TransactionPictureDto.java` — DTO
- [x] `TransactionPictureRepository.java` — repository with soft delete queries
- [x] `TransactionPictureService.java` — upload, list, delete, cleanup
- [x] `TransactionPictureController.java` — 4 endpoints (upload, list, remove, remove_unused)

### B. Import/Parse (CSV/TSV)
- [x] `TransactionImportService.java` — CSV/TSV parser with column mapping
- [x] `TransactionImportController.java` — 4 endpoints (parse_custom, parse_standard, import, process)

### C. Reconciliation
- [x] `ReconciliationService.java` — statement generation + reconcile
- [x] `ReconciliationController.java` — 2 endpoints (statements, reconcile)

### D. LLM Receipt Recognition
- [x] `LlmService.java` — stub with OpenAI integration point
- [x] `LlmController.java` — updated to call service

---

## Remaining

| Feature | Status | Notes |
|---------|--------|-------|
| **Exchange Rates** | Implemented | All 3 endpoints done in Season 4 |
| **Application Settings** | Not started | 3 endpoints, low priority |
| **LLM Full Integration** | Stub | Needs OpenAI/Anthropic API key |

---

## Files Created

```
backend/src/main/java/com/bookkeeping/core/
├── transaction/
│   ├── TransactionPicture.java         [updated]
│   ├── TransactionPictureDto.java      [new]
│   ├── TransactionPictureRepository.java [updated]
│   ├── TransactionPictureService.java  [new]
│   ├── TransactionPictureController.java [updated]
│   ├── TransactionImportService.java   [new]
│   ├── TransactionImportController.java [new]
│   ├── ReconciliationService.java      [new]
│   └── ReconciliationController.java  [new]
├── llm/
│   ├── LlmService.java                 [updated]
│   └── LlmController.java              [updated]

backend/src/main/resources/db/migration/
└── V12__transaction_pictures.sql       [new]
```

---

## Build Status

```
./gradlew compileJava  ✅
./gradlew test          117 tests (114 pass, 3 pre-existing failures)
```

---

## Next Steps

1. Push changes to GitHub (CI will run)
2. Implement Application Settings (low priority)
3. Wire up frontend for Transaction Pictures
4. Add LLM API key configuration