package com.UnderUpb.backendUnderUpb.repository;

import com.UnderUpb.backendUnderUpb.entity.SaveGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SaveRepository extends JpaRepository<SaveGame, UUID> {
    List<SaveGame> findByUserIdOrderByUpdatedDateDesc(UUID userId);
}
