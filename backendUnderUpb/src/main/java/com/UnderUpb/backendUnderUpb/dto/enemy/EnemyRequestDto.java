package com.UnderUpb.backendUnderUpb.dto.enemy;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class EnemyRequestDto {
    private String name;
    private Integer damage;
    private Integer totalLife;
    private Integer level;
    private String behaviorJson;
}
