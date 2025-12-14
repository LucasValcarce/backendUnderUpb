package com.UnderUpb.backendUnderUpb.dto.product;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProductRequestDto {
    private String name;
    private String type;
    private String description;
    private Double price;
    private String currency;
}
