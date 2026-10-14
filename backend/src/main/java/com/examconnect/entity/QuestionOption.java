package com.examconnect.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "question_options", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"question_id", "option_key"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @ToString.Exclude
    private PracticeQuestion question;

    @Column(nullable = false, length = 10)
    private String optionKey; // A, B, C, D

    @Column(nullable = false, columnDefinition = "TEXT")
    private String optionText;
}
