package com.UnderUpb.backendUnderUpb.dto.character;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CharacterResponseDto {
    private UUID id;
    private String name;
    private String description;
    private String abilities;
    private Integer requiredLevel;
    private Instant createdDate;
    private Instant updatedDate;
}
