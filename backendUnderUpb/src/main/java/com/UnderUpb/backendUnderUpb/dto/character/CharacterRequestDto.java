package com.UnderUpb.backendUnderUpb.dto.character;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CharacterRequestDto {
    private String name;
    private String description;
}
