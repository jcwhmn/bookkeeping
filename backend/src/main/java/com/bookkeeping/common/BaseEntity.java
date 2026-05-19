package com.bookkeeping.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity implements Auditable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    
    @Column(name = "created_at", nullable = false)
    protected Long createdAt;
    
    @Column(name = "updated_at", nullable = false)
    protected Long updatedAt;
    
    @Column(name = "created_by")
    protected Long createdBy;
    
    @Column(name = "modified_by")
    protected Long modifiedBy;
    
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