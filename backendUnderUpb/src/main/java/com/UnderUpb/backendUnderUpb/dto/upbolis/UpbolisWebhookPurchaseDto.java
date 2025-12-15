package com.UnderUpb.backendUnderUpb.dto.upbolis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UpbolisWebhookPurchaseDto {
    @JsonProperty("buyer_username")
    private String buyerUsername;
    
    private String product;
    
    private Double amount;
    
    @JsonProperty("product_id")
    private Long productId;
    
    @JsonProperty("transaction_id")
    private String transactionId;
    
    @JsonProperty("timestamp")
    private String timestamp;
}
