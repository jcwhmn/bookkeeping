package com.bookkeeping.common;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Base entity with common fields for all JPA entities.
 * Uses Unix timestamps for consistency with frontend.
 * 
 * All construction must use entity builders ({@code Entity.builder()...build()}).
 * Setters are eliminated from all entities.
 * Use {@link #withId(Long)} only for test fixture setup — never in production code.
 */
@MappedSuperclass
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseEntity implements Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    protected Long createdAt;
    
    @Column(name = "updated_at", nullable = false)
    protected Long updatedAt;
    
    /**
     * Set the entity ID. ONLY for test fixture setup and JPA entity hydration.
     * NEVER call this in production service code.
     */
    @SuppressWarnings("unchecked")
    public <T extends BaseEntity> T withId(Long id) {
        this.id = id;
        return (T) this;
    }
    
    @PrePersist
    protected void onCreate() {
        long now = System.currentTimeMillis() / 1000;
        this.createdAt = now;
        this.updatedAt = now;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = System.currentTimeMillis() / 1000;
    }
}