package com.UnderUpb.backendUnderUpb.dto.purchase;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PurchaseWithUpbolisTokenDto {
    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("product_id")
    private UUID productId;

    private Integer quantity;

    @JsonProperty("upbolis_token")
    private String upbolisToken;

    @JsonProperty("user_upbolis_email")
    private String userUpbolisEmail;

    private String description;
}
