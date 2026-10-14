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
public class StudyMaterialDto {
    private Long id;
    private String title;
    private String description;
    private String materialType;
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private String contentText;
    private Long examId;
    private String examName;
    private Long subjectId;
    private String subjectName;
    private Long topicId;
    private String topicName;
    private boolean active;
    private boolean bookmarked;
    private boolean completed;
    private LocalDateTime createdAt;
}
