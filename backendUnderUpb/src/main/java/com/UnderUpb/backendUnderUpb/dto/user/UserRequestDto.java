package com.UnderUpb.backendUnderUpb.dto.user;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserRequestDto {
    private String name;
    private Integer lifePoints;
    private Integer score;
    private Integer currentLevel;
    private String inventory;
}
