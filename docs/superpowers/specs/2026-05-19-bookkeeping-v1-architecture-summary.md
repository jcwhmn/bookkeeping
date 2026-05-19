# Architecture Design Summary

> This file points to detailed ADR documents. See the `../adr/` directory.

## Key Decisions

| ADR | Title | Status |
|-----|-------|--------|
| [ADR-001](adr/ADR-001-monolith.md) | Monolith Architecture | ✅ Accepted |
| [ADR-002](adr/ADR-002-package-structure.md) | Flat Package Structure | ✅ Accepted |
| [ADR-003](adr/ADR-003-dto-mapping.md) | mapstruct_plus for DTO | ✅ Accepted |
| [ADR-004](adr/ADR-004-database-design.md) | PostgreSQL + BIGINT amounts | ✅ Accepted |
| [ADR-005](adr/ADR-005-api-design.md) | REST API Design | ✅ Accepted |

## Quick Reference

### Package Structure

```
com.bookkeeping
├── common/              # Shared
├── core/               # Core Domain
│   ├── account/        # Entity + DTO + Controller + Service
│   ├── transaction/
│   ├── category/
│   ├── tag/
│   ├── budget/
│   └── report/
├── supporting/         # Supporting Subdomain
│   ├── user/
│   └── auth/
└── infrastructure/     # Cross-cutting
```

### Tech Stack

| Layer | Technology |
|-------|------------|
| Backend | Spring Boot 4.0.6 |
| Build | Gradle 9.3 (Kotlin DSL) |
| Java | OpenJDK 25 |
| Database | PostgreSQL 17+ |
| ORM | Spring Data JPA + Hibernate 6.x |
| DTO Mapping | mapstruct_plus |
| Frontend | Nuxt 4 + Vue 3 + Vuetify 3 |

### Amount Convention

All monetary values stored as **BIGINT in cents/fen**:
- `5000` represents `$50.00` or `¥50.00`
- Frontend divides by 100 for display

### Timestamp Convention

All timestamps stored as **Unix epoch seconds** (BIGINT):
- `1717104000` = 2024-05-31 00:00:00 UTC

### Response Envelope

```json
{
  "success": true|false,
  "result": { ... },
  "errorCode": 204001,
  "errorMessage": "Error description"
}
```

### Error Code Format

`category * 100000 + subCategory * 1000 + index`

| Module | SubCategory | Range |
|--------|-------------|-------|
| Auth | 01 | 201xxx |
| User | 02 | 202xxx |
| Account | 03 | 203xxx |
| Transaction | 04 | 204xxx |
| Category | 05 | 205xxx |
| Tag | 06 | 206xxx |
| Budget | 07 | 207xxx |