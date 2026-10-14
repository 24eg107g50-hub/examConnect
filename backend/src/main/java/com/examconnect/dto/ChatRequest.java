package com.examconnect.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    private Long sessionId;

    @NotBlank(message = "Message content is required")
    private String message;

    private Long examId;
    private Long subjectId;
}
