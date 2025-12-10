package com.UnderUpb.backendUnderUpb.dto.level;

import com.UnderUpb.backendUnderUpb.dto.question.QuestionResponseDto;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class LevelResponseDto {
    private UUID id;
    private String name;
    private String description;
    private Integer orderIndex;
    private List<QuestionResponseDto> questions;
    private Instant createdDate;
    private Instant updatedDate;
}
