package com.UnderUpb.backendUnderUpb.dto.savegame;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class SaveGameRequestDto {
    private UUID userId;
    private String stateJson;
    private Integer version;
}
