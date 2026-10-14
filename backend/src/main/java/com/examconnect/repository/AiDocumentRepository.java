package com.examconnect.repository;

import com.examconnect.entity.AiDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiDocumentRepository extends JpaRepository<AiDocument, Long> {
    List<AiDocument> findByStatus(String status);
    List<AiDocument> findAllByOrderByCreatedAtDesc();
}
