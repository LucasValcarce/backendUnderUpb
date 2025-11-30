package com.UnderUpb.backendUnderUpb.dto.savegame;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class SaveGameResponseDto {
    private UUID id;
    private UUID userId;
    private String stateJson;
    private Integer version;
    private Instant createdDate;
    private Instant updatedDate;
}
