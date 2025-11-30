package com.UnderUpb.backendUnderUpb.dto.decision;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class DecisionRequestDto {
    private UUID questionId;
    private String payloadJson;
    private String description;
}
