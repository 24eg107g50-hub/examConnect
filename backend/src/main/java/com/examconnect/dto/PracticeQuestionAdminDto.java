package com.examconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PracticeQuestionAdminDto {
    private Long id;

    @NotBlank(message = "Question text is required")
    private String questionText;

    @NotNull(message = "Exam is required")
    private Long examId;

    @NotNull(message = "Subject is required")
    private Long subjectId;

    private Long topicId;

    @NotBlank(message = "Correct option is required")
    private String correctOption; // A, B, C, D

    @NotBlank(message = "Explanation is required")
    private String explanation;

    @Builder.Default
    private String difficulty = "MEDIUM";

    @Builder.Default
    private Double marks = 1.0;

    @Builder.Default
    private Double negativeMarks = 0.25;

    @NotEmpty(message = "At least two options are required")
    private List<QuestionOptionDto> options;
}
