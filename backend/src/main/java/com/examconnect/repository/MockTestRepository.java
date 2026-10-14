package com.examconnect.repository;

import com.examconnect.entity.MockTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockTestRepository extends JpaRepository<MockTest, Long> {
    List<MockTest> findByExamIdAndActiveTrue(Long examId);
    Page<MockTest> findByActiveTrue(Pageable pageable);
    long countByActiveTrue();
}
