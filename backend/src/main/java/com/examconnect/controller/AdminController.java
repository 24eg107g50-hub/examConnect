package com.examconnect.controller;

import com.examconnect.ai.RagService;
import com.examconnect.dto.*;
import com.examconnect.entity.User;
import com.examconnect.repository.*;
import com.examconnect.security.UserPrincipal;
import com.examconnect.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final StudyMaterialRepository studyMaterialRepository;
    private final PracticeQuestionRepository practiceQuestionRepository;
    private final MockTestRepository mockTestRepository;
    private final DoubtRepository doubtRepository;
    private final ContentReportRepository contentReportRepository;
    private final ExamService examService;
    private final StudyMaterialService studyMaterialService;
    private final PracticeQuestionService practiceQuestionService;
    private final MockTestService mockTestService;
    private final DoubtService doubtService;
    private final RagService ragService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAdminStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", userRepository.countByRole("ROLE_STUDENT"));
        stats.put("activeExams", examRepository.count());
        stats.put("studyMaterials", studyMaterialRepository.countByActiveTrue());
        stats.put("practiceQuestions", practiceQuestionRepository.count());
        stats.put("mockTests", mockTestRepository.countByActiveTrue());
        stats.put("forumDoubts", doubtRepository.count());
        stats.put("pendingReports", contentReportRepository.countByStatus("PENDING"));

        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/students")
    public ResponseEntity<ApiResponse<PagedResponse<UserProfileDto>>> getStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {

        Page<User> studentPage = userRepository.findByRole("ROLE_STUDENT", PageRequest.of(page, size));
        Page<UserProfileDto> dtoPage = studentPage.map(u -> UserProfileDto.builder()
                .id(u.getId())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .phone(u.getPhone())
                .role(u.getRole())
                .enabled(u.isEnabled())
                .createdAt(u.getCreatedAt())
                .build());

        PagedResponse<UserProfileDto> response = PagedResponse.<UserProfileDto>builder()
                .content(dtoPage.getContent())
                .page(dtoPage.getNumber())
                .size(dtoPage.getSize())
                .totalElements(dtoPage.getTotalElements())
                .totalPages(dtoPage.getTotalPages())
                .last(dtoPage.isLast())
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // Exam Management
    @PostMapping("/exams")
    public ResponseEntity<ApiResponse<ExamDto>> createExam(@Valid @RequestBody ExamDto dto) {
        ExamDto created = examService.createExam(dto);
        return ResponseEntity.ok(ApiResponse.success("Exam created successfully", created));
    }

    @PutMapping("/exams/{id}")
    public ResponseEntity<ApiResponse<ExamDto>> updateExam(@PathVariable Long id, @Valid @RequestBody ExamDto dto) {
        ExamDto updated = examService.updateExam(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Exam updated successfully", updated));
    }

    @DeleteMapping("/exams/{id}")
    public ResponseEntity<ApiResponse<String>> deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return ResponseEntity.ok(ApiResponse.success("Exam deleted successfully", null));
    }

    // Subject & Topic Management
    @PostMapping("/subjects")
    public ResponseEntity<ApiResponse<SubjectDto>> createSubject(@Valid @RequestBody SubjectDto dto) {
        SubjectDto created = examService.createSubject(dto);
        return ResponseEntity.ok(ApiResponse.success("Subject created successfully", created));
    }

    @PostMapping("/topics")
    public ResponseEntity<ApiResponse<TopicDto>> createTopic(@Valid @RequestBody TopicDto dto) {
        TopicDto created = examService.createTopic(dto);
        return ResponseEntity.ok(ApiResponse.success("Topic created successfully", created));
    }

    // Study Material Management
    @PostMapping(value = "/materials", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<StudyMaterialDto>> createMaterial(
            @RequestPart("data") @Valid StudyMaterialRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal UserPrincipal adminUser) {

        StudyMaterialDto created = studyMaterialService.createMaterial(request, file, adminUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Material created successfully", created));
    }

    @PutMapping(value = "/materials/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<StudyMaterialDto>> updateMaterial(
            @PathVariable Long id,
            @RequestPart("data") @Valid StudyMaterialRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal UserPrincipal adminUser) {

        StudyMaterialDto updated = studyMaterialService.updateMaterial(id, request, file, adminUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Material updated successfully", updated));
    }

    @DeleteMapping("/materials/{id}")
    public ResponseEntity<ApiResponse<String>> deleteMaterial(@PathVariable Long id) {
        studyMaterialService.deleteMaterial(id);
        return ResponseEntity.ok(ApiResponse.success("Material deleted successfully", null));
    }

    // Question Bank Management
    @PostMapping("/questions")
    public ResponseEntity<ApiResponse<PracticeQuestionAdminDto>> createQuestion(
            @Valid @RequestBody PracticeQuestionAdminDto dto) {

        PracticeQuestionAdminDto created = practiceQuestionService.createQuestion(dto);
        return ResponseEntity.ok(ApiResponse.success("Question added successfully", created));
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<ApiResponse<PracticeQuestionAdminDto>> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody PracticeQuestionAdminDto dto) {

        PracticeQuestionAdminDto updated = practiceQuestionService.updateQuestion(id, dto);
        return ResponseEntity.ok(ApiResponse.success("Question updated successfully", updated));
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<ApiResponse<String>> deleteQuestion(@PathVariable Long id) {
        practiceQuestionService.deleteQuestion(id);
        return ResponseEntity.ok(ApiResponse.success("Question deleted successfully", null));
    }

    // Mock Test Management
    @PostMapping("/mock-tests")
    public ResponseEntity<ApiResponse<MockTestSummaryDto>> createMockTest(
            @Valid @RequestBody MockTestCreateRequest request) {

        MockTestSummaryDto created = mockTestService.createMockTest(request);
        return ResponseEntity.ok(ApiResponse.success("Mock test configured successfully", created));
    }

    @DeleteMapping("/mock-tests/{id}")
    public ResponseEntity<ApiResponse<String>> deleteMockTest(@PathVariable Long id) {
        mockTestService.deleteMockTest(id);
        return ResponseEntity.ok(ApiResponse.success("Mock test deleted successfully", null));
    }

    // Content Reports & Moderation
    @GetMapping("/reports")
    public ResponseEntity<ApiResponse<PagedResponse<ContentReportDto>>> getReports(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PagedResponse<ContentReportDto> reports = doubtService.getReports(status, page, size);
        return ResponseEntity.ok(ApiResponse.success(reports));
    }

    @PostMapping("/reports/{id}/resolve")
    public ResponseEntity<ApiResponse<String>> resolveReport(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {

        String status = payload.getOrDefault("status", "ACTION_TAKEN");
        doubtService.resolveReport(id, status);
        return ResponseEntity.ok(ApiResponse.success("Report updated to " + status, null));
    }

    // AI Documents Management
    @PostMapping(value = "/ai/documents", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ApiResponse<AiDocumentDto>> ingestDocument(
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "examId", required = false) Long examId,
            @RequestParam(value = "subjectId", required = false) Long subjectId,
            @RequestPart("file") MultipartFile file) {

        AiDocumentDto ingested = ragService.ingestDocument(title, description, examId, subjectId, file);
        return ResponseEntity.ok(ApiResponse.success("Document ingested and vector indexed for RAG", ingested));
    }

    @GetMapping("/ai/documents")
    public ResponseEntity<ApiResponse<List<AiDocumentDto>>> getAiDocuments() {
        List<AiDocumentDto> docs = ragService.getAllDocuments();
        return ResponseEntity.ok(ApiResponse.success(docs));
    }

    @DeleteMapping("/ai/documents/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAiDocument(@PathVariable Long id) {
        ragService.deleteDocument(id);
        return ResponseEntity.ok(ApiResponse.success("AI document removed", null));
    }
}
