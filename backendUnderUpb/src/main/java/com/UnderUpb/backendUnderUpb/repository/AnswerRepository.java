package com.UnderUpb.backendUnderUpb.repository;

import com.UnderUpb.backendUnderUpb.entity.Answers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AnswerRepository extends JpaRepository<Answers, UUID> {
    List<Answers> findByQuestionId(UUID questionId);
    List<Answers> findByQuestionIdAndIsCorrect(UUID questionId, Boolean isCorrect);
}
