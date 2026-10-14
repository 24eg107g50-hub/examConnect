package com.examconnect.controller;

import com.examconnect.dto.ApiResponse;
import com.examconnect.dto.ExamDto;
import com.examconnect.dto.SubjectDto;
import com.examconnect.dto.TopicDto;
import com.examconnect.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ExamController {

    private final ExamService examService;

    @GetMapping("/exams")
    public ResponseEntity<ApiResponse<List<ExamDto>>> getExams(
            @RequestParam(required = false) String category) {
        List<ExamDto> exams = examService.getAllExams(category);
        return ResponseEntity.ok(ApiResponse.success(exams));
    }

    @GetMapping("/exams/{id}")
    public ResponseEntity<ApiResponse<ExamDto>> getExamById(@PathVariable Long id) {
        ExamDto exam = examService.getExamById(id);
        return ResponseEntity.ok(ApiResponse.success(exam));
    }

    @GetMapping("/exams/{examId}/subjects")
    public ResponseEntity<ApiResponse<List<SubjectDto>>> getSubjectsByExam(@PathVariable Long examId) {
        List<SubjectDto> subjects = examService.getSubjectsByExamId(examId);
        return ResponseEntity.ok(ApiResponse.success(subjects));
    }

    @GetMapping("/subjects/{subjectId}/topics")
    public ResponseEntity<ApiResponse<List<TopicDto>>> getTopicsBySubject(@PathVariable Long subjectId) {
        List<TopicDto> topics = examService.getTopicsBySubjectId(subjectId);
        return ResponseEntity.ok(ApiResponse.success(topics));
    }
}
