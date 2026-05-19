# ADR-006: Version Strategy

## Status
✅ Accepted

## Context

We need a versioning strategy for releases that communicates progress and stability.

## Decision

Use **Semantic Versioning with small starting point**:

| Phase | Version | Meaning |
|-------|---------|---------|
| Development | 0.0.1 → 0.0.x | Initial development, unstable |
| First Milestone | 0.1.0 | Core features working |
| Subsequent | 0.2.0, 0.3.0, ... | Each major feature set |
| Stable | 1.0.0 | Production ready |
| Major | 2.0.0 | Breaking changes |

### Version Format

`major.minor.patch`
- **major**: Breaking changes (1.0.0 → 2.0.0)
- **minor**: New features, backward compatible (1.1.0)
- **patch**: Bug fixes (1.1.1)

### Milestone Schedule

| Milestone | Version | Features |
|----------|---------|----------|
| M1 | 0.1.0 | User auth + Accounts |
| M2 | 0.2.0 | Transactions + Categories |
| M3 | 0.3.0 | Tags + Templates |
| M4 | 0.4.0 | Budgets + Alerts |
| M5 | 0.5.0 | Reports + Export |
| M6 | 1.0.0 | Production ready |

## Rationale

- Starting at 0.0.1 acknowledges it's early/incomplete
- Moving to 0.1.0 at first milestone provides clear progress marker
- Semantic versioning is industry standard
- Clear path to 1.0.0 (production ready)

## Metadata

- **Date**: 2026-05-19
- **Author**: System
- **Status**: Accepted