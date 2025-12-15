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
    private UUID userId;
    private String name;
    private Integer score;
}
