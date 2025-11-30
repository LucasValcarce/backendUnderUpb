package com.UnderUpb.backendUnderUpb.dto.level;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class LevelResponseDto {
    private UUID id;
    private String name;
    private String description;
    private Integer requiredXp;
    private Integer orderIndex;
    private Instant createdDate;
    private Instant updatedDate;
}
