package com.UnderUpb.backendUnderUpb.dto.leaderboard;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class LeaderboardResponseDto {
    private UUID id;
    private UUID userId;
    private Integer score;
    private Instant createdDate;
    private Instant updatedDate;
}
