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
public class DashboardStatsDto {
    private String studentName;
    private long availableExams;
    private long studyMaterialsAccessed;
    private long mockTestsCompleted;
    private double averageScore;
    private long questionsPosted;
    private long answersReceived;
    private List<StudyMaterialDto> recentlyAccessedMaterials;
    private List<AttemptHistoryDto> recentTestResults;
    private List<TopicPerformanceDto> weakTopics;
    private List<SubjectPerformanceDto> subjectProgress;
    private List<RuleRecommendationDto> recommendations;
}
