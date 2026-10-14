package com.examconnect.repository;

import com.examconnect.entity.MockTestQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockTestQuestionRepository extends JpaRepository<MockTestQuestion, Long> {
    List<MockTestQuestion> findByMockTestIdOrderByOrderIndexAsc(Long mockTestId);
    void deleteByMockTestId(Long mockTestId);
}
