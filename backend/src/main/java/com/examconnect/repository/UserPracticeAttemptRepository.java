package com.examconnect.repository;

import com.examconnect.entity.UserPracticeAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserPracticeAttemptRepository extends JpaRepository<UserPracticeAttempt, Long> {
    long countByUserId(Long userId);
    long countByUserIdAndIsCorrectTrue(Long userId);
    List<UserPracticeAttempt> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT upa FROM UserPracticeAttempt upa WHERE upa.user.id = :userId")
    List<UserPracticeAttempt> findAllByUserId(@Param("userId") Long userId);
}
