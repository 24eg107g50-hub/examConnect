package com.examconnect.repository;

import com.examconnect.entity.TestAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestAttemptRepository extends JpaRepository<TestAttempt, Long> {
    List<TestAttempt> findByUserIdOrderByCreatedAtDesc(Long userId);
    Page<TestAttempt> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Optional<TestAttempt> findByIdAndUserId(Long id, Long userId);
    long countByUserId(Long userId);
    long countByUserIdAndStatus(Long userId, String status);

    @Query("SELECT AVG(ta.percentage) FROM TestAttempt ta WHERE ta.user.id = :userId AND ta.status = 'COMPLETED'")
    Double getAverageScoreByUserId(@Param("userId") Long userId);

    @Query("SELECT MAX(ta.percentage) FROM TestAttempt ta WHERE ta.user.id = :userId AND ta.status = 'COMPLETED'")
    Double getHighestScoreByUserId(@Param("userId") Long userId);

    @Query("SELECT ta FROM TestAttempt ta WHERE ta.user.id = :userId AND ta.status = 'COMPLETED' ORDER BY ta.createdAt ASC")
    List<TestAttempt> findCompletedAttemptsChronological(@Param("userId") Long userId);

    List<TestAttempt> findTop5ByUserIdAndStatusOrderByCreatedAtDesc(Long userId, String status);
}
