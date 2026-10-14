package com.examconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestQuestionAnswerDto {
    private Long questionId;
    private String selectedOption; // A, B, C, D or null
    private boolean markedForReview;
}
