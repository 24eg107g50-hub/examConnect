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
public class PracticeSubmitRequest {
    @NotNull(message = "Question ID is required")
    private Long questionId;

    @NotBlank(message = "Selected option is required")
    private String selectedOption;
}
