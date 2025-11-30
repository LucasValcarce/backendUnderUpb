package com.UnderUpb.backendUnderUpb.dto.purchase;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PurchaseOrderRequestDto {
    private UUID userId;
    private String itemName;
    private Double amount;
    private String currency;
    private String description;
}
