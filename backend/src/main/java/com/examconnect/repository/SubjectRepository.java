package com.examconnect.repository;

import com.examconnect.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByExamIdAndActiveTrue(Long examId);
    List<Subject> findByExamId(Long examId);
}
