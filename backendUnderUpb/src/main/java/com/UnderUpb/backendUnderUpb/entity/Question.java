package com.UnderUpb.backendUnderUpb.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "questions")
public class Question extends AuditableEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "text", length = 1500, nullable = false)
    private String text;

    @Column(name = "level")
    private Integer level;

    @Column(name = "options", columnDefinition = "text")
    private String optionsJson; // JSON with options and effects

    @Column(name = "answer")
    private String answer;

    @Column(name = "description", length = 1000)
    private String description;

    @PrePersist
    public void ensureId() {
        if (this.id == null) this.id = UUID.randomUUID();
    }
}
