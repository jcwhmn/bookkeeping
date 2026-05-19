# ADR-003: DTO Mapping - MapStruct-Ext

## Status
✅ Accepted (with TODO)

## Context

We need to convert between Entity and DTO objects for REST API. Options:
1. MapStruct - requires writing mapper interfaces for each conversion
2. MapStruct-Ext - annotation-based, less boilerplate (our custom implementation)
3. Manual mapping - verbose, error-prone
4. Other (BeanUtils, ModelMapper) - reflection overhead

## Decision

Use **mapstruct-ext** as external dependency instead of MapStruct mapper interfaces.

## Usage Example

```java
// TransactionDto.java
@MapperAuto(sourceEntity = Transaction.class)
public class TransactionDto {
    private Long id;
    private String type;                    // Enum -> String
    private String amountStr;               // Long -> String
    private String transactionTimeStr;      // Long -> String
    private String accountIdStr;
    private String categoryIdStr;
    private String notes;
    // Getters/Setters
}
```

## Rationale

| Aspect | Benefit |
|--------|---------|
| Boilerplate | Just add `@MapperAuto` - converter auto-generated |
| Built-in Converters | DateTime, Number, Collection, Enum included |
| Maintainability | mapstruct_plus as standalone project |
| Simplicity | Less code to maintain |

## Consequences

### Positive
- Less mapper interface code
- Built-in type conversions
- Active maintenance in separate repo

### Negative
- DTOs use **String fields** for DateTime/Number types
  - `amountStr` instead of `amount Long`
  - `transactionTimeStr` instead of `transactionTime Long`
- Requires external dependency
- Less type safety in DTOs

## TODO

- [ ] Review if String-based DTO fields are acceptable long-term
- [ ] Consider improving mapstruct-ext to support typed DTO fields (Long, LocalDateTime)
- [ ] Document DTO conversion patterns

## Metadata

- **Date**: 2026-05-19
- **Author**: System
- **Status**: Accepted
- **External Dependency**: https://github.com/jcwhmn/mapstruct-ext