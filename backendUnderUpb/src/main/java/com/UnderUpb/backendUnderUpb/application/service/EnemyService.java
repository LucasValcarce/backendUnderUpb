package com.UnderUpb.backendUnderUpb.application.service;

import com.UnderUpb.backendUnderUpb.dto.enemy.EnemyRequestDto;
import com.UnderUpb.backendUnderUpb.dto.enemy.EnemyResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface EnemyService {
    EnemyResponseDto createEnemy(EnemyRequestDto enemyDto);
    EnemyResponseDto getEnemyById(UUID enemyId);
    Page<EnemyResponseDto> getAllEnemies(Pageable pageable);
    EnemyResponseDto updateEnemy(UUID enemyId, EnemyRequestDto enemyDto);
    void deleteEnemy(UUID enemyId);
    List<EnemyResponseDto> getEnemiesByLevel(Integer level);
}
