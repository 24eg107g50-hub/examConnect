package com.examconnect.repository;

import com.examconnect.entity.PracticeQuestion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PracticeQuestionRepository extends JpaRepository<PracticeQuestion, Long> {

    @Query("SELECT q FROM PracticeQuestion q WHERE " +
           "(:examId IS NULL OR q.exam.id = :examId) AND " +
           "(:subjectId IS NULL OR q.subject.id = :subjectId) AND " +
           "(:topicId IS NULL OR q.topic.id = :topicId) AND " +
           "(:difficulty IS NULL OR LOWER(q.difficulty) = LOWER(:difficulty))")
    Page<PracticeQuestion> findFilteredQuestions(
            @Param("examId") Long examId,
            @Param("subjectId") Long subjectId,
            @Param("topicId") Long topicId,
            @Param("difficulty") String difficulty,
            Pageable pageable
    );

    List<PracticeQuestion> findByExamId(Long examId);
    List<PracticeQuestion> findBySubjectId(Long subjectId);
    long countByExamId(Long examId);
}
