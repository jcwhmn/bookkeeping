# ADR-007: Base Entity and Service Patterns

## Status
✅ Accepted

## Context

We need consistent patterns across entities and services for:
- Common fields (id, timestamps)
- Reusable base classes
- Audit trail (who created/modified)
- Reduced boilerplate

## Decision

### 1. Auditable Interface (Optional)

```java
// Auditable.java - Optional interface for audit fields
public interface Auditable {
    Long getCreatedBy();
    void setCreatedBy(Long createdBy);
    Long getModifiedBy();
    void setModifiedBy(Long modifiedBy);
}
```

### 2. Base Entity Class

```java
// BaseEntity.java
public abstract class BaseEntity implements Auditable {
    protected Long id;
    protected Boolean deleted = false;
    protected Long deletedUnixTime;
    protected Long createdUnixTime;
    protected Long updatedUnixTime;
    
    // Audit fields (from Auditable)
    protected Long createdBy;
    protected Long modifiedBy;
    
    // Getters/Setters with JPA lifecycle hooks
}
```

**Note**: `BaseEntity implements Auditable` so all entities have audit fields.
If you need an entity WITHOUT audit fields, it can implement just the base without Auditable.

### 3. Base Service Class

```java
// BaseService.java
public abstract class BaseService<T extends BaseEntity> {
    
    protected abstract BaseRepository<T> getRepository();
    
    protected Long getCurrentUserId() {
        // Get from SecurityContext
    }
    
    public Optional<T> findById(Long id) {
        return getRepository().findByIdNotDeleted(id);
    }
    
    public T save(T entity) {
        Long now = System.currentTimeMillis() / 1000;
        entity.setUpdatedUnixTime(now);
        
        if (entity.getId() == null) {
            entity.setCreatedUnixTime(now);
            entity.setCreatedBy(getCurrentUserId());
        }
        entity.setModifiedBy(getCurrentUserId());
        
        return getRepository().save(entity);
    }
    
    public void delete(T entity) {
        entity.setDeleted(true);
        entity.setDeletedUnixTime(System.currentTimeMillis() / 1000);
        getRepository().save(entity);
    }
}
```

### 4. DTO as Java Records

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

### 5. Base Repository Pattern

```java
// BaseRepository.java
public interface BaseRepository<T extends BaseEntity> 
        extends JpaRepository<T, Long> {
    
    Optional<T> findByIdNotDeleted(Long id);
    
    List<T> findAllNotDeletedByUserId(Long userId);
    
    @Query("WHERE t.deleted = false")
    List<T> findAllNotDeleted();
}
```

## Rationale

| Aspect | Decision | Reason |
|-------|----------|--------|
| Auditable Interface | `implements Auditable` | Optional audit fields per entity type |
| Base Entity | Single class with id + timestamps | DRY, consistent |
| Soft Delete | In BaseEntity | All entities support deletion |
| Audit Trail | createdBy, modifiedBy | Track WHO made changes |
| Base Service | Template methods | Reduced boilerplate, sets audit fields |
| DTO Records | Flat records, no inheritance | Simple, immutable, less nesting |
| Timestamps | Unix seconds (BIGINT) | Consistent with database design |

## Consequences

### Positive
- Less boilerplate
- Consistent entity structure
- Audit trail tracks WHO (not just WHEN)
- Immutable DTOs prevent mutation bugs
- Service layer auto-sets audit fields

### Negative
- Inheritance coupling (minimal)
- DTOs can't extend each other (use composition instead)

## Metadata

- **Date**: 2026-05-19
- **Author**: System
- **Status**: Accepted