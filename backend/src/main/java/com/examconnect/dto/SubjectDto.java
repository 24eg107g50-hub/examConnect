package com.examconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDto {
    private Long id;
    private Long examId;
    private String examName;
    private String name;
    private String code;
    private String description;
    private boolean active;
    private long topicsCount;
}
