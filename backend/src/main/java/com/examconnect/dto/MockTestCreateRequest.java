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
public class MockTestCreateRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Exam is required")
    private Long examId;

    @NotNull(message = "Duration in minutes is required")
    private Integer durationMinutes;

    private Double passingMarks;

    @Builder.Default
    private Double negativeMarkingPerQuestion = 0.25;

    @NotEmpty(message = "Question IDs are required")
    private List<Long> questionIds;
}
