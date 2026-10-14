package com.examconnect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentReportRequest {
    @NotBlank(message = "Content type is required (DOUBT or ANSWER)")
    private String contentType;

    @NotNull(message = "Content ID is required")
    private Long contentId;

    @NotBlank(message = "Reason is required")
    private String reason;

    private String details;
}
