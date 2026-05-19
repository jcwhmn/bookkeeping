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
    
    @Column(name = "deleted")
    protected Boolean deleted = false;
    
    @Column(name = "deleted_unix_time")
    protected Long deletedUnixTime;
    
    @Column(name = "created_unix_time", nullable = false)
    protected Long createdUnixTime;
    
    @Column(name = "updated_unix_time", nullable = false)
    protected Long updatedUnixTime;
    
    @Column(name = "created_by")
    protected Long createdBy;
    
    @Column(name = "modified_by")
    protected Long modifiedBy;
    
    @PrePersist
    protected void onCreate() {
        long now = System.currentTimeMillis() / 1000;
        this.createdUnixTime = now;
        this.updatedUnixTime = now;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedUnixTime = System.currentTimeMillis() / 1000;
    }
}