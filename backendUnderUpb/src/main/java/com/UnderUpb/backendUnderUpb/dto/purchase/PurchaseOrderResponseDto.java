package com.UnderUpb.backendUnderUpb.dto.purchase;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PurchaseOrderResponseDto {
    private UUID id;
    private UUID userId;
    private UUID productId;
    private String itemName;
    private Double amount;
    private String currency;
    private String description;
    private String status;
    private String externalPaymentId;
    private String paymentUrl;
    private Instant createdDate;
    private Instant updatedDate;
}
