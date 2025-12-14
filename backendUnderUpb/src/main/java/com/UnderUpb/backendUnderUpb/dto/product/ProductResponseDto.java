package com.UnderUpb.backendUnderUpb.dto.product;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ProductResponseDto {
    private UUID id;
    private String name;
    private String type;
    private String description;
    private Double price;
    private String currency;
    private Instant createdDate;
    private Instant updatedDate;
}
