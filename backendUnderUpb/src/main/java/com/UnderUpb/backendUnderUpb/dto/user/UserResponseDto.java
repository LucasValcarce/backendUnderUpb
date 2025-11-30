package com.UnderUpb.backendUnderUpb.dto.user;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

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
    private String inventory;
    private Instant createdDate;
    private Instant updatedDate;
}
