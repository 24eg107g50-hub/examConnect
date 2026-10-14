package com.examconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContentReportDto {
    private Long id;
    private Long reporterId;
    private String reporterName;
    private String contentType;
    private Long contentId;
    private String reason;
    private String details;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
