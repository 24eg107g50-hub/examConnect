package com.examconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamDto {
    private Long id;
    private String code;
    private String name;
    private String description;
    private String category;
    private boolean active;
    private long subjectsCount;
    private long materialsCount;
    private long questionsCount;
    private long mockTestsCount;
}
