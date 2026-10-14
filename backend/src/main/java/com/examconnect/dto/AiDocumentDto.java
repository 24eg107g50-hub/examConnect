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
public class AiDocumentDto {
    private Long id;
    private String title;
    private String description;
    private String fileName;
    private String fileUrl;
    private Long examId;
    private String examName;
    private Long subjectId;
    private String subjectName;
    private String status;
    private Integer chunkCount;
    private LocalDateTime createdAt;
}
