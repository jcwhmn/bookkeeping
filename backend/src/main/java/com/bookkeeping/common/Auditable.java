package com.bookkeeping.common;

/**
 * Interface for entities with audit fields.
 * Provides consistent createdAt/updatedAt tracking.
 * Only getters exposed — timestamp management is handled by JPA lifecycle callbacks.
 */
public interface Auditable {
    Long getCreatedAt();
    Long getUpdatedAt();
}
