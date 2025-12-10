package com.UnderUpb.backendUnderUpb.repository;

import com.UnderUpb.backendUnderUpb.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {
    List<Question> findByLevel(Integer level);

    @Query("SELECT q FROM Question q WHERE q.level.orderIndex = :levelNumber")
    List<Question> findByLevelNumber(@Param("levelNumber") Integer levelNumber);

    @Query("SELECT q FROM Question q WHERE q.level.id = :levelId")
    List<Question> findByLevelId(@Param("levelId") UUID levelId);
}
