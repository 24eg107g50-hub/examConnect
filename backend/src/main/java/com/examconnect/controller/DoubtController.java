package com.examconnect.controller;

import com.examconnect.dto.*;
import com.examconnect.security.UserPrincipal;
import com.examconnect.service.DoubtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DoubtController {

    private final DoubtService doubtService;

    @GetMapping("/doubts")
    public ResponseEntity<ApiResponse<PagedResponse<DoubtSummaryDto>>> getDoubts(
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) Boolean unansweredOnly,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "newest") String sortBy) {

        PagedResponse<DoubtSummaryDto> doubts = doubtService.getDoubts(
                examId, subjectId, topicId, unansweredOnly, search, page, size, sortBy);
        return ResponseEntity.ok(ApiResponse.success(doubts));
    }

    @GetMapping("/doubts/{id}")
    public ResponseEntity<ApiResponse<DoubtDetailDto>> getDoubtById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Long currentUserId = userPrincipal != null ? userPrincipal.getId() : null;
        DoubtDetailDto detail = doubtService.getDoubtById(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.success(detail));
    }

    @PostMapping("/doubts")
    public ResponseEntity<ApiResponse<DoubtDetailDto>> createDoubt(
            @Valid @RequestBody CreateDoubtRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        DoubtDetailDto created = doubtService.createDoubt(request, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Doubt posted successfully", created));
    }

    @PutMapping("/doubts/{id}")
    public ResponseEntity<ApiResponse<DoubtDetailDto>> updateDoubt(
            @PathVariable Long id,
            @Valid @RequestBody CreateDoubtRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        DoubtDetailDto updated = doubtService.updateDoubt(id, request, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Doubt updated successfully", updated));
    }

    @DeleteMapping("/doubts/{id}")
    public ResponseEntity<ApiResponse<String>> deleteDoubt(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        boolean isAdmin = userPrincipal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        doubtService.deleteDoubt(id, userPrincipal.getId(), isAdmin);
        return ResponseEntity.ok(ApiResponse.success("Doubt deleted successfully", null));
    }

    @PostMapping("/doubts/{id}/answers")
    public ResponseEntity<ApiResponse<AnswerDto>> createAnswer(
            @PathVariable Long id,
            @Valid @RequestBody CreateAnswerRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        AnswerDto answer = doubtService.createAnswer(id, request, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Answer posted successfully", answer));
    }

    @PutMapping("/answers/{id}")
    public ResponseEntity<ApiResponse<AnswerDto>> updateAnswer(
            @PathVariable Long id,
            @Valid @RequestBody CreateAnswerRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        AnswerDto updated = doubtService.updateAnswer(id, request, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Answer updated successfully", updated));
    }

    @DeleteMapping("/answers/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAnswer(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        boolean isAdmin = userPrincipal.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        doubtService.deleteAnswer(id, userPrincipal.getId(), isAdmin);
        return ResponseEntity.ok(ApiResponse.success("Answer deleted successfully", null));
    }

    @PostMapping("/answers/{id}/vote")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> voteAnswer(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        int upvotes = doubtService.toggleAnswerVote(id, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Vote recorded", Map.of("upvotesCount", upvotes)));
    }

    @PostMapping("/doubts/{doubtId}/answers/{answerId}/accept")
    public ResponseEntity<ApiResponse<String>> acceptAnswer(
            @PathVariable Long doubtId,
            @PathVariable Long answerId,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        doubtService.acceptAnswer(doubtId, answerId, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Accepted answer status updated", null));
    }

    @PostMapping("/reports")
    public ResponseEntity<ApiResponse<ContentReportDto>> reportContent(
            @Valid @RequestBody ContentReportRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        ContentReportDto report = doubtService.reportContent(request, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Content report submitted for moderation", report));
    }
}
