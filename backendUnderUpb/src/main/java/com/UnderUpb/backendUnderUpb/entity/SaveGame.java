package com.UnderUpb.backendUnderUpb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "saves")
public class SaveGame extends AuditableEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Lob
    @Column(name = "state", columnDefinition = "text")
    private String stateJson;

    @Column(name = "version")
    private Integer version;

    @PrePersist
    public void ensureId() {
        if (this.id == null) this.id = UUID.randomUUID();
        if (this.version == null) this.version = 1;
    }
}
