package com.examconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleRecommendationDto {
    private String topicName;
    private String subjectName;
    private String severity; // HIGH, MEDIUM, LOW
    private String recommendationText;
    private Long subjectId;
    private Long topicId;
}
