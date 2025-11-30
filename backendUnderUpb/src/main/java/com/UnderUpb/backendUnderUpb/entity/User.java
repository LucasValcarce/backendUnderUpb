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
@Table(name = "players")
public class User extends AuditableEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", length = 150, nullable = false)
    private String name;

    @Column(name = "life_points")
    private Integer lifePoints;

    @Column(name = "score")
    private Integer score;

    @Column(name = "current_level")
    private Integer currentLevel;

    @Column(name = "inventory", columnDefinition = "text")
    private String inventory; // json

    @PrePersist
    public void ensureId() {
        if (this.id == null) this.id = UUID.randomUUID();
        if (this.lifePoints == null) this.lifePoints = 3;
        if (this.score == null) this.score = 0;
        if (this.currentLevel == null) this.currentLevel = 1;
    }
}
