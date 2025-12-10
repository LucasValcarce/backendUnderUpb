package com.UnderUpb.backendUnderUpb.dto.purchase;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PurchasedItemDto {
    private UUID purchaseId;
    private UUID productId;
    private String name;
    private String type;
    private Integer quantity;
    private Double price;
    private String currency;
    private String status;
    private Instant purchasedAt;
}
