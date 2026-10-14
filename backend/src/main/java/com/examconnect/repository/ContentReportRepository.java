package com.examconnect.repository;

import com.examconnect.entity.ContentReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentReportRepository extends JpaRepository<ContentReport, Long> {
    Page<ContentReport> findByStatus(String status, Pageable pageable);
    long countByStatus(String status);
}
