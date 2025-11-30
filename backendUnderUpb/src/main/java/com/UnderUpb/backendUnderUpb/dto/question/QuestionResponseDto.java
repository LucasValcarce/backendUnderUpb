package com.UnderUpb.backendUnderUpb.dto.question;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class QuestionResponseDto {
    private UUID id;
    private String text;
    private Integer level;
    private String optionsJson;
    private String answer;
    private String description;
    private Instant createdDate;
    private Instant updatedDate;
}
