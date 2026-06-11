package com.bookkeeping.common;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.function.Function;

/**
 * Base entity with common fields for all JPA entities.
 * Uses Unix timestamps for consistency with frontend.
 *
 * All construction must use entity builders ({@code Entity.builder()...build()}).
 * Setters are eliminated from all entities.
 * Use {@link #withId(Long)} only for test fixture setup — never in production code.
 *
 * <h2>Important: The {@code toBuilder()} bug</h2>
 * Lombok's {@code @Builder(toBuilder = true)} on child entities does NOT
 * include the {@code id} field (which lives in this class). Calling
 * {@code entity.toBuilder().someField(x).build()} produces a NEW entity with
 * {@code id == null}, which Hibernate then INSERTs as a duplicate row
 * instead of UPDATEing the existing one.
 *
 * <p>To safely update an entity, use {@link #applyUpdate(Function)} which
 * preserves the id and createdAt fields automatically.</p>
 *
 * <p>Usage:</p>
 * <pre>{@code
 * Category updated = existing.applyUpdate(b -> b
 *     .name(newName)
 *     .sortOrder(5)
 *     .build());
 * categoryRepository.save(updated); // UPDATE, not INSERT
 * }</pre>
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

    /**
     * Apply changes to a copy of this entity, preserving the {@code id} and
     * {@code createdAt} fields that Lombok's {@code toBuilder()} does not copy.
     *
     * <p>This is the SAFE replacement for {@code entity.toBuilder()...build()}
     * when the entity's id is declared in a superclass.</p>
     *
     * <p>Usage:</p>
     * <pre>{@code
     * Category updated = existing.applyUpdate(b -> b
     *     .name(newName)
     *     .sortOrder(5)
     *     .build());
     * }</pre>
     *
     * <p>Note: the lambda parameter is the entity itself (not the builder).
     * Call {@code entity.toBuilder()} inside the lambda to access the builder.</p>
     *
     * @param updater function that takes the source entity, uses {@code toBuilder()}
     *                to apply field changes, and returns the built entity
     * @param <T>     the entity type
     * @return the updated entity with id and createdAt preserved
     */
    @SuppressWarnings("unchecked")
    public <T extends BaseEntity> T applyUpdate(Function<T, T> updater) {
        T self = (T) this;
        T built = updater.apply(self);
        // Preserve the inherited fields that Lombok's toBuilder doesn't copy
        built.id = this.id;
        built.createdAt = this.createdAt;
        return built;
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
