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
public class BookmarkDto {
    private Long id;
    private Long materialId;
    private String title;
    private String materialType;
    private String examName;
    private String subjectName;
    private String topicName;
    private LocalDateTime bookmarkedAt;
}
