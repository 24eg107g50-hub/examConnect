package com.examconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MockTestSummaryDto {
    private Long id;
    private String title;
    private String description;
    private Long examId;
    private String examName;
    private Integer durationMinutes;
    private Integer totalQuestions;
    private Double totalMarks;
    private Double passingMarks;
    private Double negativeMarkingPerQuestion;
    private boolean active;
}
