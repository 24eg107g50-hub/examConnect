package com.examconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestResultDto {
    private Long attemptId;
    private Long mockTestId;
    private String testTitle;
    private String examName;
    private Double totalScore;
    private Double maxScore;
    private Double percentage;
    private Integer correctCount;
    private Integer incorrectCount;
    private Integer unansweredCount;
    private Integer totalQuestions;
    private Double accuracy;
    private Integer timeTakenSeconds;
    private Boolean passed;
    private LocalDateTime submitTime;
    private Map<String, SubjectPerformanceDto> subjectWisePerformance;
    private Map<String, TopicPerformanceDto> topicWisePerformance;
    private List<QuestionReviewDto> questionReviews;
}
