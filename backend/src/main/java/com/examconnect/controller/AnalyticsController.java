package com.examconnect.controller;

import com.examconnect.dto.*;
import com.examconnect.security.UserPrincipal;
import com.examconnect.service.AnalyticsService;
import com.examconnect.service.MockTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final MockTestService mockTestService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getDashboardStats(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        DashboardStatsDto stats = analyticsService.getDashboardStats(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/analytics/performance")
    public ResponseEntity<ApiResponse<PerformanceAnalyticsDto>> getPerformanceAnalytics(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        PerformanceAnalyticsDto analytics = analyticsService.getPerformanceAnalytics(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(analytics));
    }

    @GetMapping("/analytics/subjects")
    public ResponseEntity<ApiResponse<List<SubjectPerformanceDto>>> getSubjectAnalytics(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        PerformanceAnalyticsDto analytics = analyticsService.getPerformanceAnalytics(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(analytics.getSubjectPerformance()));
    }

    @GetMapping("/analytics/topics")
    public ResponseEntity<ApiResponse<List<TopicPerformanceDto>>> getTopicAnalytics(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        PerformanceAnalyticsDto analytics = analyticsService.getPerformanceAnalytics(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(analytics.getWeakTopics()));
    }

    @GetMapping("/analytics/history")
    public ResponseEntity<ApiResponse<List<AttemptHistoryDto>>> getAttemptHistory(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        List<AttemptHistoryDto> history = mockTestService.getUserHistory(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(history));
    }
}
