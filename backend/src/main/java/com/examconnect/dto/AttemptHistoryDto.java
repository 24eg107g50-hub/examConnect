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
public class AttemptHistoryDto {
    private Long attemptId;
    private Long mockTestId;
    private String testTitle;
    private String examName;
    private Double score;
    private Double maxScore;
    private Double percentage;
    private Boolean passed;
    private Integer timeTakenSeconds;
    private String status;
    private LocalDateTime date;
}
