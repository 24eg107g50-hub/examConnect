package com.examconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DoubtDetailDto {
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
    private String attachmentUrl;
    private boolean hasAcceptedAnswer;
    private Long acceptedAnswerId;
    private Integer answersCount;
    private Integer viewsCount;
    private boolean isOwner;
    private LocalDateTime createdAt;
    private List<AnswerDto> answers;
}
