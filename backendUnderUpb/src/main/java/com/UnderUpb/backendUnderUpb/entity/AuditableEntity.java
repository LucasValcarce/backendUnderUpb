package com.UnderUpb.backendUnderUpb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@MappedSuperclass
@Getter
@Setter
public abstract class AuditableEntity implements Serializable {

    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant createdDate;

    @Column(name = "updated_date")
    private Instant updatedDate;

    @PrePersist
    public void prePersist() {
        this.createdDate = Instant.now();
        this.updatedDate = Instant.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedDate = Instant.now();
    }
}
