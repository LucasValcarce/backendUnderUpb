package com.UnderUpb.backendUnderUpb.dto.user;

import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import com.UnderUpb.backendUnderUpb.dto.purchase.PurchasedItemDto;
import com.UnderUpb.backendUnderUpb.dto.owned.OwnedProductDto;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserResponseDto {
    private UUID id;
    private String name;
    private Integer lifePoints;
    private Integer score;
    private Integer currentLevel;
    private Instant createdDate;
    private Instant updatedDate;
    private List<PurchasedItemDto> purchases;
    private List<OwnedProductDto> ownedProducts;
}
