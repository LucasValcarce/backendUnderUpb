package com.UnderUpb.backendUnderUpb.repository;

import com.UnderUpb.backendUnderUpb.entity.Enemy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EnemyRepository extends JpaRepository<Enemy, UUID> {
    List<Enemy> findByLevel(Integer level);
}
