package com.UnderUpb.backendUnderUpb.dto.answer;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class AnswerResponseDto {
    private UUID id;
    private UUID questionId;
    private String text;
    private Boolean isCorrect;
    private String explanation;
    private Instant createdDate;
    private Instant updatedDate;
}
