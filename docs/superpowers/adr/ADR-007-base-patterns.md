# ADR-007: Base Entity and Service Patterns

## Status
✅ Accepted

## Context

We need consistent patterns across entities and services for:
- Common fields (id, timestamps)
- Reusable base classes
- Reduced boilerplate

## Decision

### 1. Base Entity Class

```java
// BaseEntity.java
public abstract class BaseEntity {
    protected Long id;
    protected Boolean deleted = false;
    protected Long deletedUnixTime;
    protected Long createdUnixTime;
    protected Long updatedUnixTime;
    
    // Getters/Setters with JPA lifecycle hooks
    // @PrePersist, @PreUpdate
}
```

All entities extend `BaseEntity`:

```java
// Account.java
@Entity
@Table(name = "accounts")
public class Account extends BaseEntity {
    private String name;
    private String type;
    private String currency;
    private Long balance; // in cents
    // ... other fields
}
```

### 2. Base Service Class

```java
// BaseService.java
public abstract class BaseService<T extends BaseEntity> {
    
    protected abstract BaseRepository<T> getRepository();
    
    public Optional<T> findById(Long id) {
        return getRepository().findByIdNotDeleted(id);
    }
    
    public T save(T entity) {
        entity.setUpdatedUnixTime(System.currentTimeMillis() / 1000);
        if (entity.getId() == null) {
            entity.setCreatedUnixTime(entity.getUpdatedUnixTime());
        }
        return getRepository().save(entity);
    }
    
    public void delete(T entity) {
        entity.setDeleted(true);
        entity.setDeletedUnixTime(System.currentTimeMillis() / 1000);
        getRepository().save(entity);
    }
}
```

### 3. DTO as Java Records

DTOs are immutable **Java Records** (no hierarchical structure):

```java
// AccountDto.java
public record AccountDto(
    String idStr,           // null-safe String
    String name,
    String type,
    String currency,
    String balanceStr,      // Long -> String for mapstruct-ext
    String icon,
    String color,
    String notes,
    String includeInTotalStr,
    String archivedStr
) {
    // Records are immutable, compact syntax
    // All fields are final by default
}
```

### 4. Base Repository Pattern

```java
// BaseRepository.java
public interface BaseRepository<T extends BaseEntity> 
        extends JpaRepository<T, Long> {
    
    Optional<T> findByIdNotDeleted(Long id);
    
    List<T> findAllNotDeletedByUserId(Long userId);
    
    @Override
    @Query("WHERE deleted = false")
    List<T> findAll();
}
```

## Rationale

| Aspect | Decision | Reason |
|-------|----------|--------|
| Base Entity | Single class with id + timestamps | DRY, consistent |
| Soft Delete | In BaseEntity | All entities support deletion |
| Base Service | Template methods | Reduced boilerplate in services |
| DTO Records | Flat records, no inheritance | Simple, immutable, less nesting |
| Timestamps | Unix seconds (BIGINT) | Consistent with database design |

## Consequences

### Positive
- Less boilerplate
- Consistent entity structure
- Easy to add common fields
- Immutable DTOs prevent mutation bugs

### Negative
- Inheritance coupling (minimal)
- DTOs can't extend each other (use composition instead)

## Metadata

- **Date**: 2026-05-19
- **Author**: System
- **Status**: Accepted