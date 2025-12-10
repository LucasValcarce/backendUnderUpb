package com.UnderUpb.backendUnderUpb.application.service;

import com.UnderUpb.backendUnderUpb.dto.gameplay.LevelCompletionDto;
import com.UnderUpb.backendUnderUpb.dto.gameplay.LevelCompletionResponseDto;

public interface GameplayService {
    LevelCompletionResponseDto completeLevel(LevelCompletionDto completionDto);
}
