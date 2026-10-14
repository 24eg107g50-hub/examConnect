package com.examconnect.repository;

import com.examconnect.entity.AiDocumentChunk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AiDocumentChunkRepository extends JpaRepository<AiDocumentChunk, Long> {
    List<AiDocumentChunk> findByDocumentIdOrderByChunkIndexAsc(Long documentId);

    @Query("SELECT c FROM AiDocumentChunk c WHERE c.document.status = 'PROCESSED'")
    List<AiDocumentChunk> findAllProcessedChunks();
}
