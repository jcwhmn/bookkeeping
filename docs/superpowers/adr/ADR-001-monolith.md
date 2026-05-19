# ADR-001: Architecture Style - Monolith

## Status
✅ Accepted

## Context

We need to build a bookkeeping application for a small team (~20 members). The v1.0 scope is bounded with clear domain requirements:
- Account, Transaction, Category, Tag, Budget, Template, Report (Core Domain)
- User, Auth, Token (Supporting Subdomain)

## Decision

Use **monolithic architecture** with clear domain separation (Core Domain + Supporting Subdomain).

```
┌─────────────────────────────────────────────────────────────┐
│                      Frontend (Nuxt 4)                      │
└─────────────────────────────┬───────────────────────────────┘
                              │ REST API
┌─────────────────────────────┴───────────────────────────────┐
│                      Backend (Spring Boot)                   │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐  │
│  │   Core      │  │  Supporting │  │    Infrastructure   │  │
│  │  Domain     │  │  Subdomain  │  │                     │  │
│  └─────────────┘  └─────────────┘  └─────────────────────┘  │
└─────────────────────────────┬───────────────────────────────┘
                              │
                        PostgreSQL
```

## Rationale

| Factor | Decision | Reason |
|--------|----------|--------|
| Team Size | ~20 members | Small team, no need for microservices complexity |
| Scope | v1.0 is bounded | All features fit in one deployment unit |
| Complexity | Minimize ops | Single database, single deployment |
| Iteration Speed | Fast iteration | Easier to develop and test locally |
| Future Scale | Can split later | Domain separation allows future extraction |

## Consequences

### Positive
- Single deployment unit (backend + frontend)
- Shared database (PostgreSQL) - ACID transactions easy
- Domain separation for code organization only
- Microservices extraction possible when needed

### Negative
- All modules scale together
- Bug in one module affects all
- Deployment requires coordination

## When to Reconsider

Consider microservices when:
- Team grows to 50+ developers
- Need independent scaling of components
- Different deployment cadences for modules
- Performance isolation requirements

## Metadata

- **Date**: 2026-05-19
- **Author**: System
- **Status**: Accepted