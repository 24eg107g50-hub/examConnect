package com.examconnect.ai;

import com.examconnect.dto.*;
import com.examconnect.entity.*;
import com.examconnect.exception.ForbiddenException;
import com.examconnect.exception.ResourceNotFoundException;
import com.examconnect.repository.*;
import com.examconnect.service.FileStorageService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RagService {

    private final AiDocumentRepository documentRepository;
    private final AiDocumentChunkRepository chunkRepository;
    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;
    private final FileStorageService fileStorageService;
    private final PdfExtractorService pdfExtractorService;
    private final EmbeddingService embeddingService;
    private final VectorSearchService vectorSearchService;
    private final LlmService llmService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public ChatResponse processChat(ChatRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ChatSession session;
        if (request.getSessionId() != null) {
            session = sessionRepository.findById(request.getSessionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Chat session not found"));
            if (!session.getUser().getId().equals(userId)) {
                throw new ForbiddenException("Cannot access another user's chat session");
            }
        } else {
            String title = request.getMessage().length() > 40
                    ? request.getMessage().substring(0, 40) + "..."
                    : request.getMessage();
            session = ChatSession.builder().user(user).title(title).build();
            session = sessionRepository.save(session);
        }

        // Save User Message
        ChatMessage userMessage = ChatMessage.builder()
                .session(session)
                .senderType("USER")
                .content(request.getMessage().trim())
                .build();
        messageRepository.save(userMessage);

        // Vector Retrieval
        List<VectorSearchService.ScoredChunk> scoredChunks = vectorSearchService.searchSimilarChunks(
                request.getMessage(), request.getExamId(), request.getSubjectId(), 4, 0.15);

        List<SourceCitationDto> citations = vectorSearchService.toCitations(scoredChunks);

        // LLM / RAG generation
        LlmService.LlmGenerationResult result = llmService.generateAnswer(request.getMessage(), scoredChunks);

        String citationsJson = null;
        try {
            citationsJson = objectMapper.writeValueAsString(citations);
        } catch (Exception e) {
            log.error("Failed to serialize citations", e);
        }

        // Save Assistant Message
        ChatMessage assistantMessage = ChatMessage.builder()
                .session(session)
                .senderType("ASSISTANT")
                .content(result.answer())
                .sourcesJson(citationsJson)
                .build();
        messageRepository.save(assistantMessage);

        return ChatResponse.builder()
                .sessionId(session.getId())
                .response(result.answer())
                .citations(citations)
                .groundedInDocuments(!scoredChunks.isEmpty())
                .modelUsed(result.modelName())
                .fallbackMode(result.fallbackMode())
                .notice(result.notice())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ChatSessionDto> getUserSessions(Long userId) {
        return sessionRepository.findByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(s -> ChatSessionDto.builder()
                        .id(s.getId())
                        .title(s.getTitle())
                        .createdAt(s.getCreatedAt())
                        .updatedAt(s.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getSessionMessages(Long sessionId, Long userId) {
        ChatSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        if (!session.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Cannot access another user's chat session");
        }

        return messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId).stream()
                .map(m -> {
                    List<SourceCitationDto> citations = null;
                    if (m.getSourcesJson() != null) {
                        try {
                            citations = objectMapper.readValue(m.getSourcesJson(), new TypeReference<List<SourceCitationDto>>() {});
                        } catch (Exception ignored) {}
                    }
                    return ChatMessageDto.builder()
                            .id(m.getId())
                            .senderType(m.getSenderType())
                            .content(m.getContent())
                            .citations(citations)
                            .createdAt(m.getCreatedAt())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // Document Ingestion
    @Transactional
    public AiDocumentDto ingestDocument(String title, String description, Long examId, Long subjectId, MultipartFile file) {
        String fileUrl = fileStorageService.storeFile(file, "ai_docs");
        Exam exam = examId != null ? examRepository.findById(examId).orElse(null) : null;
        Subject subject = subjectId != null ? subjectRepository.findById(subjectId).orElse(null) : null;

        AiDocument doc = AiDocument.builder()
                .title(title)
                .description(description)
                .fileName(file.getOriginalFilename())
                .fileUrl(fileUrl)
                .exam(exam)
                .subject(subject)
                .status("PROCESSING")
                .chunkCount(0)
                .build();

        AiDocument savedDoc = documentRepository.save(doc);

        try {
            Path filePath = fileStorageService.getFilePath(fileUrl);
            File actualFile = filePath.toFile();

            List<PdfExtractorService.ExtractedPage> pages;
            if (file.getOriginalFilename() != null && file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
                pages = pdfExtractorService.extractPages(actualFile);
            } else {
                String text = new String(file.getBytes());
                pages = List.of(new PdfExtractorService.ExtractedPage(1, text));
            }

            List<PdfExtractorService.DocumentChunk> chunks = pdfExtractorService.chunkText(pages, 1200, 200);

            for (PdfExtractorService.DocumentChunk c : chunks) {
                float[] embedding = embeddingService.generateEmbedding(c.content());
                String embedJson = embeddingService.vectorToJson(embedding);

                AiDocumentChunk chunk = AiDocumentChunk.builder()
                        .document(savedDoc)
                        .chunkIndex(c.chunkIndex())
                        .pageNumber(c.pageNumber())
                        .content(c.content())
                        .embeddingJson(embedJson)
                        .build();

                chunkRepository.save(chunk);
            }

            savedDoc.setStatus("PROCESSED");
            savedDoc.setChunkCount(chunks.size());
            documentRepository.save(savedDoc);
            log.info("Successfully ingested AI document '{}' with {} chunks", title, chunks.size());

        } catch (Exception e) {
            log.error("Failed to parse and index AI document", e);
            savedDoc.setStatus("FAILED");
            documentRepository.save(savedDoc);
        }

        return mapToDocDto(savedDoc);
    }

    @Transactional(readOnly = true)
    public List<AiDocumentDto> getAllDocuments() {
        return documentRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToDocDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteDocument(Long id) {
        if (!documentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Document not found with ID: " + id);
        }
        documentRepository.deleteById(id);
    }

    private AiDocumentDto mapToDocDto(AiDocument doc) {
        return AiDocumentDto.builder()
                .id(doc.getId())
                .title(doc.getTitle())
                .description(doc.getDescription())
                .fileName(doc.getFileName())
                .fileUrl(doc.getFileUrl())
                .examId(doc.getExam() != null ? doc.getExam().getId() : null)
                .examName(doc.getExam() != null ? doc.getExam().getName() : null)
                .subjectId(doc.getSubject() != null ? doc.getSubject().getId() : null)
                .subjectName(doc.getSubject() != null ? doc.getSubject().getName() : null)
                .status(doc.getStatus())
                .chunkCount(doc.getChunkCount())
                .createdAt(doc.getCreatedAt())
                .build();
    }
}
