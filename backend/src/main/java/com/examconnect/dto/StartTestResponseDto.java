package com.examconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartTestResponseDto {
    private Long attemptId;
    private Long mockTestId;
    private String testTitle;
    private String examName;
    private Integer durationMinutes;
    private LocalDateTime startTime;
    private LocalDateTime deadline;
    private Integer totalQuestions;
    private Double totalMarks;
    private Double negativeMarkingPerQuestion;
    private List<PracticeQuestionDto> questions;
}
