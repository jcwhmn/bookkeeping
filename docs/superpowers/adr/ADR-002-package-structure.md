# ADR-002: Package Structure - Flat per Domain

## Status
✅ Accepted

## Context

We want to minimize package nesting. Traditional Java projects use deep nesting:
```
controller/
service/
repository/
dto/
entity/
mapper/
```

This leads to 4-6 levels of directories just to find related code.

## Decision

Organize code by **domain package** with Entity + DTO + Controller + Service **in the same package**.

```
com.bookkeeping
├── core/
│   ├── account/
│   │   ├── Account.java           # Entity
│   │   ├── AccountDto.java        # DTO
│   │   ├── AccountController.java # REST API
│   │   └── AccountService.java    # Business logic
│   ├── transaction/
│   │   ├── Transaction.java
│   │   ├── TransactionDto.java
│   │   ├── TransactionController.java
│   │   └── TransactionService.java
│   └── ...
├── supporting/
│   ├── user/
│   │   ├── User.java
│   │   ├── UserDto.java
│   │   ├── UserController.java
│   │   └── UserService.java
│   └── auth/
│       └── ...
└── common/
    └── ...
```

## Rationale

| Aspect | Benefit |
|--------|---------|
| Navigation | Less drilling through directories |
| Co-location | Entity, DTO, Controller, Service together |
| Ownership | Clear ownership per domain package |
| Simplicity | Similar to Go-style flat packages |
| IDE Support | Package-private classes easier to access |

## Consequences

### Positive
- Faster file lookup
- Related code stays together
- Less boilerplate directory creation

### Negative
- Slightly larger packages
- Some classes in same file (acceptable)

## Metadata

- **Date**: 2026-05-19
- **Author**: System
- **Status**: Accepted