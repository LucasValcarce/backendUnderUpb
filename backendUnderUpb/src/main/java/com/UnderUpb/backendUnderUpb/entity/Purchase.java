package com.UnderUpb.backendUnderUpb.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "purchases")
public class Purchase extends AuditableEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "price")
    private Double price;

    @Column(name = "currency", length = 10)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private PurchaseStatus status;

    @Column(name = "purchased_at")
    private Instant purchasedAt;

    @Column(name = "external_payment_id", length = 200)
    private String externalPaymentId;

    @Column(name = "payment_url", length = 2000)
    private String paymentUrl;

    @PrePersist
    public void ensureId() {
        if (this.id == null) this.id = UUID.randomUUID();
        if (this.purchasedAt == null) this.purchasedAt = Instant.now();
    }
}
