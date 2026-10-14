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
public class AnswerDto {
    private Long id;
    private Long doubtId;
    private Long userId;
    private String userName;
    private String content;
    private boolean isAccepted;
    private Integer upvotesCount;
    private boolean hasVoted;
    private boolean isOwner;
    private LocalDateTime createdAt;
}
