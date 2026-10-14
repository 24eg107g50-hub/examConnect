package com.examconnect.controller;

import com.examconnect.dto.*;
import com.examconnect.security.UserPrincipal;
import com.examconnect.service.MockTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MockTestController {

    private final MockTestService mockTestService;

    @GetMapping("/mock-tests")
    public ResponseEntity<ApiResponse<List<MockTestSummaryDto>>> getMockTests(
            @RequestParam(required = false) Long examId) {
        List<MockTestSummaryDto> tests = mockTestService.getAllMockTests(examId);
        return ResponseEntity.ok(ApiResponse.success(tests));
    }

    @GetMapping("/mock-tests/{id}")
    public ResponseEntity<ApiResponse<MockTestSummaryDto>> getMockTestById(@PathVariable Long id) {
        MockTestSummaryDto test = mockTestService.getMockTestById(id);
        return ResponseEntity.ok(ApiResponse.success(test));
    }

    @PostMapping("/mock-tests/{id}/attempts")
    public ResponseEntity<ApiResponse<StartTestResponseDto>> startMockTest(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        StartTestResponseDto response = mockTestService.startTest(id, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Test session started", response));
    }

    @PostMapping("/attempts/{id}/submit")
    public ResponseEntity<ApiResponse<TestResultDto>> submitMockTest(
            @PathVariable Long id,
            @RequestBody SubmitTestRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        TestResultDto result = mockTestService.submitTest(id, request, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Test submitted successfully", result));
    }

    @GetMapping("/attempts/{id}/result")
    public ResponseEntity<ApiResponse<TestResultDto>> getAttemptResult(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        TestResultDto result = mockTestService.getResult(id, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/attempts/history")
    public ResponseEntity<ApiResponse<List<AttemptHistoryDto>>> getAttemptHistory(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        List<AttemptHistoryDto> history = mockTestService.getUserHistory(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}
