package com.examconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceAnalyticsDto {
    private long totalTestsAttempted;
    private long totalTestsCompleted;
    private double averageScore;
    private double highestScore;
    private double overallAccuracy;
    private long totalQuestionsAttempted;
    private long totalCorrectQuestions;
    private long totalIncorrectQuestions;
    private long totalMaterialsAccessed;
    private long totalMaterialsCompleted;
    private List<ScoreTrendDto> scoreTrends;
    private List<SubjectPerformanceDto> subjectPerformance;
    private List<TopicPerformanceDto> weakTopics;
    private List<RuleRecommendationDto> ruleRecommendations;
}
