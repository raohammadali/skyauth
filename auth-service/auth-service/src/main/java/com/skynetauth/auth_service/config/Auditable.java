package com.skynetauth.auth_service.config;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Auditable {

    @CreatedDate
    @Column(name = "created_at", nullable = true, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = true)
    private LocalDateTime updatedAt;
    
    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;
    
    /**
     * Initialize audit timestamps when the entity is first persisted.
     *
     * Sets {@code createdAt} and {@code updatedAt} to the current local date-time
     * immediately before the entity is persisted.
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
 
    /**
     * Sets the `updatedAt` timestamp to the current time just before the entity is updated.
     *
     * Called as a JPA `@PreUpdate` lifecycle callback.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
 
    /**
     * Populate the entity's `deletedAt` timestamp immediately before it is removed.
     *
     * This method is invoked as a JPA `@PreRemove` lifecycle callback and sets
     * `deletedAt` to the current system time.
     */
    @PreRemove
    protected void onDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
