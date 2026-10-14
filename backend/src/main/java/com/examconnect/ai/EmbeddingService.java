package com.examconnect.ai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class EmbeddingService {

    private static final int VECTOR_DIMENSION = 128;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public float[] generateEmbedding(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new float[VECTOR_DIMENSION];
        }

        // Semantic hash & n-gram term-frequency vector generation
        float[] vector = new float[VECTOR_DIMENSION];
        String normalized = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ");
        String[] words = normalized.split("\\s+");

        for (String word : words) {
            if (word.length() < 2) continue;
            int hash = Math.abs(word.hashCode());
            int idx1 = hash % VECTOR_DIMENSION;
            int idx2 = (hash / VECTOR_DIMENSION) % VECTOR_DIMENSION;
            vector[idx1] += 1.0f;
            vector[idx2] += 0.5f;

            // Character bigrams for morphological similarity
            for (int i = 0; i < word.length() - 1; i++) {
                int biHash = Math.abs((word.charAt(i) * 31 + word.charAt(i + 1))) % VECTOR_DIMENSION;
                vector[biHash] += 0.2f;
            }
        }

        // L2 Normalization
        double sumSq = 0.0;
        for (float v : vector) {
            sumSq += v * v;
        }

        if (sumSq > 0) {
            float magnitude = (float) Math.sqrt(sumSq);
            for (int i = 0; i < VECTOR_DIMENSION; i++) {
                vector[i] /= magnitude;
            }
        }

        return vector;
    }

    public double calculateCosineSimilarity(float[] vec1, float[] vec2) {
        if (vec1 == null || vec2 == null || vec1.length != vec2.length) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            normA += vec1[i] * vec1[i];
            normB += vec2[i] * vec2[i];
        }

        if (normA <= 0 || normB <= 0) return 0.0;
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    public String vectorToJson(float[] vector) {
        try {
            return objectMapper.writeValueAsString(vector);
        } catch (Exception e) {
            log.error("Failed to serialize vector to JSON", e);
            return "[]";
        }
    }

    public float[] jsonToVector(String json) {
        if (json == null || json.trim().isEmpty() || json.equals("[]")) {
            return null;
        }
        try {
            List<Double> list = objectMapper.readValue(json, new TypeReference<List<Double>>() {});
            float[] vector = new float[list.size()];
            for (int i = 0; i < list.size(); i++) {
                vector[i] = list.get(i).floatValue();
            }
            return vector;
        } catch (Exception e) {
            log.error("Failed to parse vector JSON", e);
            return null;
        }
    }
}
