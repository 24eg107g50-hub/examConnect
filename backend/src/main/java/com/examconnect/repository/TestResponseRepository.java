package com.examconnect.repository;

import com.examconnect.entity.TestResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestResponseRepository extends JpaRepository<TestResponse, Long> {
    List<TestResponse> findByAttemptId(Long attemptId);
    Optional<TestResponse> findByAttemptIdAndQuestionId(Long attemptId, Long questionId);
}
