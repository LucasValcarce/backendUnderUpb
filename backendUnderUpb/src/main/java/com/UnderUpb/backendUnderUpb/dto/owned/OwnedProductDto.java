package com.UnderUpb.backendUnderUpb.dto.owned;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class OwnedProductDto {
    private UUID ownedProductId;
    private UUID productId;
    private String sku;
    private String name;
    private String type;
    private String description;
    private Boolean isActive;
    private Boolean equipped;
    private UUID purchaseId;
    private Instant acquiredAt;
}
