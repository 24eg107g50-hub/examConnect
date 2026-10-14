package com.examconnect.repository;

import com.examconnect.entity.Doubt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoubtRepository extends JpaRepository<Doubt, Long> {

    @Query("SELECT d FROM Doubt d WHERE " +
           "(:examId IS NULL OR d.exam.id = :examId) AND " +
           "(:subjectId IS NULL OR d.subject.id = :subjectId) AND " +
           "(:topicId IS NULL OR d.topic.id = :topicId) AND " +
           "(:unansweredOnly = false OR d.answersCount = 0) AND " +
           "(:search IS NULL OR LOWER(d.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(d.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Doubt> searchDoubts(
            @Param("examId") Long examId,
            @Param("subjectId") Long subjectId,
            @Param("topicId") Long topicId,
            @Param("unansweredOnly") boolean unansweredOnly,
            @Param("search") String search,
            Pageable pageable
    );

    List<Doubt> findTop5ByOrderByCreatedAtDesc();
    List<Doubt> findByUserIdOrderByCreatedAtDesc(Long userId);
    long countByUserId(Long userId);
}
