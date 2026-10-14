package com.examconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SourceCitationDto {
    private String documentTitle;
    private Integer pageNumber;
    private String snippet;
    private Double relevanceScore;
}
