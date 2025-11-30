package com.UnderUpb.backendUnderUpb.dto.enemy;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class EnemyResponseDto {
    private UUID id;
    private String name;
    private Integer damage;
    private Integer totalLife;
    private Integer level;
    private String behaviorJson;
    private Instant createdDate;
    private Instant updatedDate;
}
