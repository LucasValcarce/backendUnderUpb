package com.UnderUpb.backendUnderUpb.dto.upbolis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UpbolisCreateProductDto {
    private String name;
    private Double price;
    private String description;
    private Integer stock;

    @JsonProperty("is_active")
    private Boolean isActive;
}
