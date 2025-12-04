package com.UnderUpb.backendUnderUpb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "answers")
public class Answers extends AuditableEntity {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(name = "text", length = 500, nullable = false)
    private String text;

    @Column(name = "is_correct")
    private Boolean isCorrect;

    @Column(name = "explanation", length = 1000)
    private String explanation;

    @PrePersist
    public void ensureId() {
        if (this.id == null) this.id = UUID.randomUUID();
    }
}
