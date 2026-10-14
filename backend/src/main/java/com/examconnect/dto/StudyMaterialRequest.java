package com.examconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyMaterialRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotBlank(message = "Material type is required")
    private String materialType; // PDF, TEXT_NOTES, SUMMARY, PRACTICE_RESOURCE

    private String contentText;

    @NotNull(message = "Exam is required")
    private Long examId;

    @NotNull(message = "Subject is required")
    private Long subjectId;

    private Long topicId;

    @Builder.Default
    private boolean active = true;
}
