package com.examconnect.repository;

import com.examconnect.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByDoubtIdOrderByIsAcceptedDescUpvotesCountDescCreatedAtDesc(Long doubtId);
    List<Answer> findByUserId(Long userId);
    long countByUserId(Long userId);

    @Query("SELECT COUNT(a) FROM Answer a WHERE a.doubt.user.id = :userId")
    long countAnswersReceivedForUserQuestions(@Param("userId") Long userId);
}
