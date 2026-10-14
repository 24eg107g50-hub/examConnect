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
public class QuestionReviewDto {
    private Long questionId;
    private String questionText;
    private String subjectName;
    private String topicName;
    private List<QuestionOptionDto> options;
    private String selectedOption;
    private String correctOption;
    private Boolean isCorrect;
    private Double marksAwarded;
    private String explanation;
    private boolean markedForReview;
}
