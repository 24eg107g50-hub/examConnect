package com.examconnect.ai;

import com.examconnect.dto.SourceCitationDto;
import com.examconnect.entity.AiDocumentChunk;
import com.examconnect.repository.AiDocumentChunkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class VectorSearchService {

    private final AiDocumentChunkRepository chunkRepository;
    private final EmbeddingService embeddingService;

    @Value("${ai.similarity-threshold:0.15}")
    private double defaultSimilarityThreshold;

    @Value("${ai.top-k:4}")
    private int defaultTopK;

    public record ScoredChunk(AiDocumentChunk chunk, double similarity) {}

    @Transactional(readOnly = true)
    public List<ScoredChunk> searchSimilarChunks(String query, Long examId, Long subjectId, Integer topK, Double minThreshold) {
        float[] queryEmbedding = embeddingService.generateEmbedding(query);

        List<AiDocumentChunk> chunks = chunkRepository.findAllProcessedChunks();
        List<ScoredChunk> results = new ArrayList<>();

        double threshold = (minThreshold != null) ? minThreshold : defaultSimilarityThreshold;
        int limit = (topK != null) ? topK : defaultTopK;

        for (AiDocumentChunk chunk : chunks) {
            // Filter by exam and subject if specified
            if (examId != null && chunk.getDocument().getExam() != null &&
                !chunk.getDocument().getExam().getId().equals(examId)) {
                continue;
            }
            if (subjectId != null && chunk.getDocument().getSubject() != null &&
                !chunk.getDocument().getSubject().getId().equals(subjectId)) {
                continue;
            }

            float[] chunkVec = embeddingService.jsonToVector(chunk.getEmbeddingJson());
            if (chunkVec == null) {
                // If chunk vector wasn't computed yet, compute and update in-memory
                chunkVec = embeddingService.generateEmbedding(chunk.getContent());
            }

            double sim = embeddingService.calculateCosineSimilarity(queryEmbedding, chunkVec);
            if (sim >= threshold) {
                results.add(new ScoredChunk(chunk, sim));
            }
        }

        results.sort((a, b) -> Double.compare(b.similarity(), a.similarity()));

        if (results.size() > limit) {
            return results.subList(0, limit);
        }
        return results;
    }

    public List<SourceCitationDto> toCitations(List<ScoredChunk> scoredChunks) {
        List<SourceCitationDto> citations = new ArrayList<>();
        for (ScoredChunk sc : scoredChunks) {
            String snippet = sc.chunk().getContent();
            if (snippet.length() > 220) {
                snippet = snippet.substring(0, 220) + "...";
            }
            citations.add(SourceCitationDto.builder()
                    .documentTitle(sc.chunk().getDocument().getTitle())
                    .pageNumber(sc.chunk().getPageNumber())
                    .snippet(snippet)
                    .relevanceScore(Math.round(sc.similarity() * 100.0) / 100.0)
                    .build());
        }
        return citations;
    }
}
