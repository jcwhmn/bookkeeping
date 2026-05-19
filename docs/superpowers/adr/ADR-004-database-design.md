# ADR-004: Database Design

## Status
✅ Accepted

## Context

We need to store financial data with:
- Accurate amount calculations (no floating point errors)
- Multi-currency support
- Audit trail (soft delete)
- Multi-user with data isolation

## Decision

| Aspect | Design | Example |
|--------|--------|---------|
| Database | PostgreSQL 17+ | - |
| Amount | BIGINT (cents/fen) | 5000 = $50.00 |
| Timestamps | Unix epoch seconds (BIGINT) | 1717104000 |
| Soft Delete | `deleted` flag + `deleted_unix_time` | - |
| Multi-tenancy | User isolation via `user_id` | - |

### Amount Convention

All monetary values stored as **BIGINT in smallest unit**:
- USD: cents (1/100 of dollar)
- CNY: fen (1/100 of yuan)
- JPY: yen (no subunit)

Frontend divide by 100 for display, multiply by 100 for input.

### Timestamp Convention

All timestamps stored as **Unix epoch seconds** (not milliseconds):
```java
// Java
System.currentTimeMillis() / 1000  // Unix seconds
```

### Soft Delete Pattern

```sql
ALTER TABLE transactions ADD COLUMN deleted BOOLEAN DEFAULT FALSE;
ALTER TABLE transactions ADD COLUMN deleted_unix_time BIGINT;
```

Queries always filter `WHERE deleted = FALSE`.

## Rationale

| Aspect | Decision | Reason |
|--------|----------|--------|
| BIGINT amounts | Integer math | No 0.1 + 0.2 = 0.30000000000000004 |
| Unix timestamps | Standard format | Easy serialization, timezone handling |
| Soft delete | Data preservation | Accidental deletion recoverable |
| User isolation | Query filter | Simple, no row-level security complexity |

## Consequences

### Positive
- Accurate financial calculations
- Consistent timestamp format
- Data recoverable after accidental deletion

### Negative
- Frontend must divide by 100 for display
- API uses BIGINT, not Decimal
- Need consistent `/100` convention across codebase

## Metadata

- **Date**: 2026-05-19
- **Author**: System
- **Status**: Accepted