package com.examconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "ai_document_chunks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiDocumentChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    @ToString.Exclude
    private AiDocument document;

    @Column(nullable = false)
    private Integer chunkIndex;

    private Integer pageNumber;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String metadataJson;

    @Column(columnDefinition = "LONGTEXT")
    private String embeddingJson;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
