package com.UnderUpb.backendUnderUpb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "levels")
public class Level extends AuditableEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", length = 120, nullable = false)
    private String name;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "order_index")
    private Integer orderIndex;

    @OneToMany(mappedBy = "level", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Question> questions = new ArrayList<>();

    @PrePersist
    public void ensureId() {
        if (this.id == null) this.id = UUID.randomUUID();
    }
}
