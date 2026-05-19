# Architecture Decision Records (ADR)

## Meta

- **Project**: Bookkeeping
- **Version**: 1.0.0
- **Date**: 2026-05-19
- **Status**: In Progress

---

## ADR-001: Monolithic Architecture

### Status
✅ Accepted

### Context
We need to build a bookkeeping application for a small team (~20 members). The v1.0 scope is bounded with clear domain requirements.

### Decision
Use **monolithic architecture** with clear domain separation (Core Domain + Supporting Subdomain).

### Rationale
| Factor | Decision | Reason |
|--------|----------|--------|
| Team Size | ~20 members | Small team, no need for microservices complexity |
| Scope | v1.0 is bounded | All features fit in one deployment unit |
| Complexity | Minimize ops | Single database, single deployment |
| Iteration Speed | Fast iteration | Easier to develop and test locally |
| Future Scale | Can split later | Domain separation allows future extraction |

### Consequences
- Single deployment unit (backend + frontend)
- Shared database (PostgreSQL)
- Domain separation for code organization only
- Microservices extraction possible when needed

---

## ADR-002: Package Structure - Flat per Domain

### Status
✅ Accepted

### Context
We want to minimize package nesting. Traditional Java projects use deep nesting (controller/, service/, repository/, dto/). We prefer flatter structure.

### Decision
Organize code by **domain package** with Entity + DTO + Controller + Service **in the same package**.

### Structure
```
com.bookkeeping
├── core/
│   ├── account/
│   │   ├── Account.java           # Entity
│   │   ├── AccountDto.java       # DTO
│   │   ├── AccountController.java
│   │   └── AccountService.java
│   ├── transaction/
│   │   └── ...
│   └── ...
├── supporting/
│   ├── user/
│   │   └── ...
│   └── auth/
│       └── ...
└── common/
    └── ...
```

### Rationale
- **Less deep nesting** - easier to navigate
- **Related code together** - entity, dto, controller, service co-located
- **Faster file lookup** - no drilling through 4+ directories
- **Similar to Go projects** - flat, pragmatic

### Consequences
- Slightly larger packages (acceptable trade-off)
- Clear ownership per domain package

---

## ADR-003: MapStruct Plus for DTO Mapping

### Status
✅ Accepted

### Context
We need to convert between Entity and DTO objects. MapStruct is the standard approach but requires writing mapper interfaces for each conversion. We have `mapstruct_plus` which reduces boilerplate.

### Decision
Use **mapstruct_plus** as external dependency instead of MapStruct mapper interfaces.

### Rationale
- **Less boilerplate** - Just add `@MapperAuto` annotation on DTO
- **Built-in converters** - DateTime, Number, Collection, Enum included
- **Maintain separately** - `mapstruct_plus` as standalone project
- **Simpler DTO design** - Auto-converts common types

### Consequences
- DTOs use **String fields** for DateTime/Number types (e.g., `amountStr` instead of `amount Long`)
- Requires `mapstruct_plus` as dependency
- Conversion happens automatically via annotation processing

### TODO
- [ ] DTO field type refactor - move from typed fields to String fields
- [ ] Evaluate if this is acceptable long-term

---

## ADR-004: Spring Modulith Not Used

### Status
✅ Rejected

### Context
Spring Modulith provides structural enforcement for modular architecture.

### Decision
**Do not use Spring Modulith** for v1.0.

### Rationale
- **Complexity** - Module system adds overhead
- **Team size** - ~20 members doesn't require compile-time enforcement
- **Flat packages** - Convention-based organization sufficient
- **Future option** - Can add modulith later if needed

### Consequences
- No compile-time domain boundary enforcement
- Package naming convention defines boundaries
- Easier initial development

---

## ADR-005: Database Design - PostgreSQL with BIGINT Amounts

### Status
✅ Accepted

### Context
We need to store financial amounts accurately and support multi-currency.

### Decision
- **Database**: PostgreSQL 17+
- **Amount Storage**: BIGINT (in cents/fen) - no floating point
- **Timestamps**: Unix epoch seconds (BIGINT)
- **Soft Delete**: `deleted` flag + `deleted_unix_time`

### Rationale
- **No floating point errors** - 0.1 + 0.2 ≠ 0.30000000000000004
- **Integer arithmetic** - exact calculations
- **Standard timestamps** - easy serialization/deserialization
- **Audit trail** - soft delete preserves data

### Consequences
- Frontend must divide by 100 for display
- All API responses use cents/fen
- Database queries use BIGINT comparisons

---

## ADR-006: REST API Design

### Status
✅ Accepted

### Context
We need a standard API design for the bookkeeping application.

### Decision
- **Base URL**: `/api/v1`
- **Response Envelope**: `{success, result, errorCode, errorMessage}`
- **Error Code Format**: `category * 100000 + subCategory * 1000 + index`
- **Authentication**: JWT Bearer Token

### Rationale
- **Versioned API** - `/api/v1` allows future `/api/v2`
- **Consistent response** - clients always know structure
- **Standard error codes** - easy debugging
- **Stateless auth** - JWT is stateless and scalable

### Consequences
- All endpoints return same envelope format
- Frontend must handle both success and error responses
- Token refresh required before expiration

---

## ADR-007: Account Sharing Model

### Status
✅ Accepted

### Context
Team members need to share certain accounts (e.g., team expenses).

### Decision
Implement **account-level sharing** with permission control.

### Data Model
```sql
-- accounts table
owner_id BIGINT NOT NULL,
shared_with BIGINT[]  -- array of user IDs

-- account_sharing table (alternative)
account_id BIGINT,
shared_with_user_id BIGINT,
permission VARCHAR(20)  -- READ_ONLY, READ_WRITE
```

### Rationale
- **Granular control** - share selected accounts only
- **Permission levels** - read-only vs read-write
- **Owner keeps control** - can revoke access anytime

### Consequences
- Queries must filter by owner or shared_with
- Transactions inherit account's shared visibility
- Future: team/group accounts as extension

---

## Future ADRs (Planned)

| ADR | Topic | Status |
|-----|-------|--------|
| ADR-008 | Frontend State Management | Pending |
| ADR-009 | Caching Strategy | Pending |
| ADR-010 | API Rate Limiting | Pending |
| ADR-011 | File Upload/Storage | Pending |
| ADR-012 | Email Service | Pending |

---

## Template

```markdown
## ADR-NNN: [Title]

### Status
[Proposed | Accepted | Rejected | Deprecated | Superseded]

### Context
[What is the issue that motivates this decision?]

### Decision
[What is the change that we're proposing and/or doing?]

### Rationale
[What is the reasoning behind the decision? Include pros and cons, references, etc.]

### Consequences
[What becomes easier or more difficult because of this decision?]

### TODO
[Any follow-up tasks or items to revisit]
```