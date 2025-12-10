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
@Table(name = "owned_products")
public class OwnedProduct extends AuditableEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id")
    private Purchase purchase;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "equipped")
    private Boolean equipped;

    @PrePersist
    public void ensureId() {
        if (this.id == null) this.id = UUID.randomUUID();
        if (this.isActive == null) this.isActive = Boolean.TRUE;
        if (this.equipped == null) this.equipped = Boolean.FALSE;
    }
}
