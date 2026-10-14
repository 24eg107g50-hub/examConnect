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
public class DoubtSummaryDto {
    private Long id;
    private String title;
    private String description;
    private Long userId;
    private String userName;
    private Long examId;
    private String examName;
    private Long subjectId;
    private String subjectName;
    private Long topicId;
    private String topicName;
    private boolean hasAcceptedAnswer;
    private Integer answersCount;
    private Integer viewsCount;
    private LocalDateTime createdAt;
}
