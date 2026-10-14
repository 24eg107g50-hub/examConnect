package com.examconnect.repository;

import com.examconnect.entity.AnswerVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnswerVoteRepository extends JpaRepository<AnswerVote, Long> {
    Optional<AnswerVote> findByAnswerIdAndUserId(Long answerId, Long userId);
    boolean existsByAnswerIdAndUserId(Long answerId, Long userId);
    void deleteByAnswerIdAndUserId(Long answerId, Long userId);
    long countByAnswerId(Long answerId);
}
