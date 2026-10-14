package com.examconnect.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "test_responses", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"attempt_id", "question_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    @ToString.Exclude
    private TestAttempt attempt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id", nullable = false)
    private PracticeQuestion question;

    @Column(length = 10)
    private String selectedOption; // A, B, C, D or null

    private Boolean isCorrect;

    @Builder.Default
    private Double marksAwarded = 0.0;

    @Builder.Default
    private Boolean markedForReview = false;
}
