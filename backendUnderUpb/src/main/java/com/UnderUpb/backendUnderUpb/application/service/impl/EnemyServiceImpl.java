package com.UnderUpb.backendUnderUpb.application.service.impl;

import com.UnderUpb.backendUnderUpb.application.service.EnemyService;
import com.UnderUpb.backendUnderUpb.dto.enemy.EnemyRequestDto;
import com.UnderUpb.backendUnderUpb.dto.enemy.EnemyResponseDto;
import com.UnderUpb.backendUnderUpb.entity.Enemy;
import com.UnderUpb.backendUnderUpb.repository.EnemyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class EnemyServiceImpl implements EnemyService {

    private final EnemyRepository enemyRepository;

    @Override
    @Transactional
    public EnemyResponseDto createEnemy(EnemyRequestDto enemyDto) {
        if (enemyDto == null || enemyDto.getName() == null) {
            throw new IllegalArgumentException("Enemy data cannot be null");
        }
        Enemy enemy = Enemy.builder()
                .name(enemyDto.getName())
                .damage(enemyDto.getDamage())
                .totalLife(enemyDto.getTotalLife())
                .level(enemyDto.getLevel())
                .build();
        Enemy savedEnemy = enemyRepository.save(enemy);
        log.info("Enemy created with ID: {}", savedEnemy.getId());
        return toResponseDto(savedEnemy);
    }

    @Override
    @Transactional(readOnly = true)
    public EnemyResponseDto getEnemyById(UUID enemyId) {
        Enemy enemy = enemyRepository.findById(enemyId)
                .orElseThrow(() -> new IllegalArgumentException("Enemy not found with ID: " + enemyId));
        return toResponseDto(enemy);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EnemyResponseDto> getAllEnemies(Pageable pageable) {
        return enemyRepository.findAll(pageable)
                .map(this::toResponseDto);
    }

    @Override
    @Transactional
    public EnemyResponseDto updateEnemy(UUID enemyId, EnemyRequestDto enemyDto) {
        Enemy enemy = enemyRepository.findById(enemyId)
                .orElseThrow(() -> new IllegalArgumentException("Enemy not found with ID: " + enemyId));
        if (enemyDto.getName() != null) {
            enemy.setName(enemyDto.getName());
        }
        if (enemyDto.getDamage() != null) {
            enemy.setDamage(enemyDto.getDamage());
        }
        if (enemyDto.getTotalLife() != null) {
            enemy.setTotalLife(enemyDto.getTotalLife());
        }
        if (enemyDto.getLevel() != null) {
            enemy.setLevel(enemyDto.getLevel());
        }
        Enemy updatedEnemy = enemyRepository.save(enemy);
        log.info("Enemy updated with ID: {}", enemyId);
        return toResponseDto(updatedEnemy);
    }

    @Override
    @Transactional
    public void deleteEnemy(UUID enemyId) {
        if (!enemyRepository.existsById(enemyId)) {
            throw new IllegalArgumentException("Enemy not found with ID: " + enemyId);
        }
        enemyRepository.deleteById(enemyId);
        log.info("Enemy deleted with ID: {}", enemyId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnemyResponseDto> getEnemiesByLevel(Integer level) {
        return enemyRepository.findByLevel(level).stream()
                .map(this::toResponseDto)
                .toList();
    }

    private EnemyResponseDto toResponseDto(Enemy enemy) {
        return EnemyResponseDto.builder()
                .id(enemy.getId())
                .name(enemy.getName())
                .damage(enemy.getDamage())
                .totalLife(enemy.getTotalLife())
                .level(enemy.getLevel())
                .createdDate(enemy.getCreatedDate())
                .updatedDate(enemy.getUpdatedDate())
                .build();
    }
}
