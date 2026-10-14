package com.examconnect.repository;

import com.examconnect.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    Optional<Exam> findByCode(String code);
    List<Exam> findByActiveTrue();
    List<Exam> findByCategoryIgnoreCaseAndActiveTrue(String category);
    boolean existsByCode(String code);
}
