# ADR-005: API Design

## Status
✅ Accepted

## Context

We need a standard API design for all REST endpoints:
- Versioning strategy
- Response format
- Error handling
- Authentication

## Decision

### Base URL
```
/api/v1/...
```

### Response Envelope

All responses follow same format:

```json
// Success
{
  "success": true,
  "result": { ... },
  "errorCode": null,
  "errorMessage": null
}

// Error
{
  "success": false,
  "result": null,
  "errorCode": 204001,
  "errorMessage": "Transaction not found"
}
```

### Error Code Format

`category * 100000 + subCategory * 1000 + index`

| Category | Range | Description |
|----------|-------|-------------|
| 1 | 1xxxxx | System errors |
| 2 | 2xxxxx | Business errors |

Business error subcategories:
| SubCategory | Range | Module |
|-------------|-------|--------|
| 01 | 201xxx | Auth |
| 02 | 202xxx | User |
| 03 | 203xxx | Account |
| 04 | 204xxx | Transaction |
| 05 | 205xxx | Category |
| 06 | 206xxx | Tag |
| 07 | 207xxx | Budget |

### Authentication

- Bearer Token (JWT)
- Header: `Authorization: Bearer <token>`

### Pagination

Cursor-based pagination using `transaction_time`:

```
GET /api/v1/transactions?limit=20&cursor=1717104000
```

## Rationale

| Aspect | Decision | Reason |
|--------|----------|--------|
| Versioning | URL path | Clear, easy to route, visible in docs |
| Envelope | Consistent | Clients always know response structure |
| Error codes | Numeric range | Programmatic handling, i18n |
| Cursor pagination | transaction_time | Stable, efficient for large datasets |
| JWT | Stateless | Scalable, no server-side session storage |

## Consequences

### Positive
- API versioning at URL level
- Easy error handling by error code
- Stateless auth scales well

### Negative
- URL changes with version
- JWT token management needed
- Cursor pagination less intuitive than offset

## Example Endpoints

```
# Auth
POST   /api/v1/auth/register
POST   /api/v1/auth/login
POST   /api/v1/auth/logout
POST   /api/v1/auth/refresh

# Accounts
GET    /api/v1/accounts
POST   /api/v1/accounts
GET    /api/v1/accounts/{id}
PUT    /api/v1/accounts/{id}
DELETE /api/v1/accounts/{id}

# Transactions
GET    /api/v1/transactions
POST   /api/v1/transactions
GET    /api/v1/transactions/{id}
PUT    /api/v1/transactions/{id}
DELETE /api/v1/transactions/{id}
```

## Metadata

- **Date**: 2026-05-19
- **Author**: System
- **Status**: Accepted