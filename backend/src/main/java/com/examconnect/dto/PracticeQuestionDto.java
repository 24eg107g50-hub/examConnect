package com.examconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PracticeQuestionDto {
    private Long id;
    private String questionText;
    private Long examId;
    private String examName;
    private Long subjectId;
    private String subjectName;
    private Long topicId;
    private String topicName;
    private String difficulty;
    private Double marks;
    private Double negativeMarks;
    private List<QuestionOptionDto> options;
    // Hidden during initial fetch for tests/unanswered questions; populated after practice submit or in review
    private String correctOption;
    private String explanation;
}
