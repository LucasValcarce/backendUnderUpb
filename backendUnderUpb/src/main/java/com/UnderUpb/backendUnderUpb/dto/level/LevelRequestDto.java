package com.UnderUpb.backendUnderUpb.dto.level;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class LevelRequestDto {
    private String name;
    private String description;
    private Integer orderIndex;
}
