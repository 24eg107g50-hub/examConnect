package com.examconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicPerformanceDto {
    private String topicName;
    private String subjectName;
    private int totalAttempted;
    private int correctCount;
    private int incorrectCount;
    private double accuracy;
    private boolean isWeakTopic;
}
