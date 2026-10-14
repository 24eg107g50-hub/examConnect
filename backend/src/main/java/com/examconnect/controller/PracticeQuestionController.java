package com.examconnect.controller;

import com.examconnect.dto.*;
import com.examconnect.security.UserPrincipal;
import com.examconnect.service.PracticeQuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PracticeQuestionController {

    private final PracticeQuestionService questionService;

    @GetMapping("/questions")
    public ResponseEntity<ApiResponse<PagedResponse<PracticeQuestionDto>>> getQuestions(
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) String difficulty,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PagedResponse<PracticeQuestionDto> questions = questionService.getQuestionsForPractice(
                examId, subjectId, topicId, difficulty, page, size);
        return ResponseEntity.ok(ApiResponse.success(questions));
    }

    @GetMapping("/questions/{id}")
    public ResponseEntity<ApiResponse<PracticeQuestionDto>> getQuestionById(@PathVariable Long id) {
        PracticeQuestionDto question = questionService.getQuestionById(id);
        return ResponseEntity.ok(ApiResponse.success(question));
    }

    @PostMapping("/practice/submit")
    public ResponseEntity<ApiResponse<PracticeSubmitResponse>> submitPracticeAnswer(
            @Valid @RequestBody PracticeSubmitRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        PracticeSubmitResponse response = questionService.submitPracticeAnswer(request, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Response recorded", response));
    }
}
