package com.examconnect.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Slf4j
public class LlmService {

    @Value("${ai.gemini.api-key:}")
    private String geminiApiKey;

    @Value("${ai.gemini.model:gemini-1.5-flash}")
    private String geminiModel;

    @Value("${ai.openai.api-key:}")
    private String openaiApiKey;

    @Value("${ai.openai.model:gpt-4o-mini}")
    private String openaiModel;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public record LlmGenerationResult(String answer, String modelName, boolean fallbackMode, String notice) {}

    public LlmGenerationResult generateAnswer(String userQuestion, List<VectorSearchService.ScoredChunk> retrievedChunks) {
        StringBuilder contextBuilder = new StringBuilder();
        if (!retrievedChunks.isEmpty()) {
            contextBuilder.append("=== RETRIEVED STUDY MATERIALS & SYLLABUS KNOWLEDGE ===\n");
            int i = 1;
            for (VectorSearchService.ScoredChunk sc : retrievedChunks) {
                contextBuilder.append(String.format("[%d] Document: '%s', Page %d:\n%s\n\n",
                        i++,
                        sc.chunk().getDocument().getTitle(),
                        sc.chunk().getPageNumber() != null ? sc.chunk().getPageNumber() : 1,
                        sc.chunk().getContent()));
            }
        }

        // Try Gemini if configured
        if (geminiApiKey != null && !geminiApiKey.trim().isEmpty() && !geminiApiKey.contains("your_")) {
            try {
                String response = callGemini(userQuestion, contextBuilder.toString());
                return new LlmGenerationResult(response, "Google Gemini (" + geminiModel + ")", false, null);
            } catch (Exception e) {
                log.error("Error invoking Gemini API: {}", e.getMessage());
            }
        }

        // Try OpenAI if configured
        if (openaiApiKey != null && !openaiApiKey.trim().isEmpty() && !openaiApiKey.contains("your_")) {
            try {
                String response = callOpenAi(userQuestion, contextBuilder.toString());
                return new LlmGenerationResult(response, "OpenAI (" + openaiModel + ")", false, null);
            } catch (Exception e) {
                log.error("Error invoking OpenAI API: {}", e.getMessage());
            }
        }

        // Grounded educational response based on actual RAG retrieved documents
        String notice = "Notice: Running in ExamConnect Grounded Knowledge Mode. To enable full generative LLM answers, configure GEMINI_API_KEY or OPENAI_API_KEY in application.yml or .env.";

        if (retrievedChunks.isEmpty()) {
            String answer = "I searched through the uploaded government examination syllabus, compendiums, and notes, but could not find high-relevance source documents specifically answering: \"" + userQuestion + "\".\n\n" +
                    "Tip: Try asking about Indian Polity (Articles 12-35, writs), Modern History (Gandhian movements, Dandi March, 1857 Revolt), Banking Awareness (CRR, RBI tools), or State Exam topics!";
            return new LlmGenerationResult(answer, "ExamConnect RAG Retrieval Engine", true, notice);
        }

        StringBuilder answer = new StringBuilder();
        answer.append("Based on the official study materials indexed in the ExamConnect knowledge base:\n\n");

        for (int i = 0; i < retrievedChunks.size(); i++) {
            VectorSearchService.ScoredChunk sc = retrievedChunks.get(i);
            answer.append(String.format("### %d. %s (Page %d)\n",
                    i + 1,
                    sc.chunk().getDocument().getTitle(),
                    sc.chunk().getPageNumber() != null ? sc.chunk().getPageNumber() : 1));
            answer.append(sc.chunk().getContent()).append("\n\n");
        }

        answer.append("---\n*These findings were retrieved with vector similarity score: ")
              .append(Math.round(retrievedChunks.get(0).similarity() * 100.0) / 100.0)
              .append(".*");

        return new LlmGenerationResult(answer.toString(), "ExamConnect RAG Retrieval Engine", true, notice);
    }

    private String callGemini(String question, String context) throws Exception {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + geminiModel + ":generateContent?key=" + geminiApiKey;

        String prompt = "You are ExamConnect AI, an expert tutor for Indian government exams (UPSC, SSC, Banking, Railways, State PSCs).\n" +
                "Provide accurate, clear, pedagogical explanations grounded in the retrieved study materials provided below.\n" +
                "Cite document titles and page numbers when relevant.\n\n" +
                context + "\n\nStudent Question: " + question;

        Map<String, Object> part = Map.of("text", prompt);
        Map<String, Object> content = Map.of("parts", List.of(part));
        Map<String, Object> body = Map.of("contents", List.of(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        return root.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();
    }

    private String callOpenAi(String question, String context) throws Exception {
        String url = "https://api.openai.com/v1/chat/completions";

        String systemPrompt = "You are ExamConnect AI, an expert educational assistant for Indian competitive exam students.\n" +
                "Answer using the retrieved context when relevant. Clearly distinguish verified facts and cite sources.\n\n" + context;

        Map<String, String> sysMsg = Map.of("role", "system", "content", systemPrompt);
        Map<String, String> userMsg = Map.of("role", "user", "content", question);

        Map<String, Object> body = Map.of(
                "model", openaiModel,
                "messages", List.of(sysMsg, userMsg),
                "temperature", 0.4
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openaiApiKey);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        JsonNode root = objectMapper.readTree(response.getBody());
        return root.path("choices").get(0).path("message").path("content").asText();
    }
}
