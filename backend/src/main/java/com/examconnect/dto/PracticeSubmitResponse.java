package com.examconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PracticeSubmitResponse {
    private Long questionId;
    private String selectedOption;
    private String correctOption;
    private boolean isCorrect;
    private String explanation;
    private long totalQuestionsPracticed;
    private long totalCorrect;
    private double overallAccuracy;
}
