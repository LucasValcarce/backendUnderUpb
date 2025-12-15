package com.UnderUpb.backendUnderUpb.dto.upbolis;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UpbolisProductDto {
    // Upbolis may return `product_name` or `name`
    @JsonAlias({"product_name", "name"})
    private String productName;
    
    private Double price;
    
    private String description;
    
    @JsonProperty("product_id")
    @JsonAlias({"id"})
    private Long productId;
    
    @JsonProperty("seller_id")
    private Long sellerId;
    
    @JsonProperty("is_active")
    private Boolean active;

    private Integer stock;

    public String getName() {
        return this.productName;
    }
}
