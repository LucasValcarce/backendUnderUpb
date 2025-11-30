package com.UnderUpb.backendUnderUpb.dto.decision;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class DecisionResponseDto {
    private UUID id;
    private UUID questionId;
    private String payloadJson;
    private String description;
    private Instant createdDate;
    private Instant updatedDate;
}
