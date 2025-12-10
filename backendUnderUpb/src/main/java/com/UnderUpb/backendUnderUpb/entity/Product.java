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
@Table(name = "products")
public class Product extends AuditableEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "sku", length = 120, nullable = false, unique = true)
    private String sku;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "type", length = 100)
    private String type; // e.j., BUFF, SKIN

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "price")
    private Double price;

    @Column(name = "currency", length = 10)
    private String currency;

    @PrePersist
    public void ensureId() {
        if (this.id == null) this.id = UUID.randomUUID();
    }
}
