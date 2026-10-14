package com.examconnect.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "mock_test_questions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"mock_test_id", "question_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MockTestQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mock_test_id", nullable = false)
    @ToString.Exclude
    private MockTest mockTest;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id", nullable = false)
    private PracticeQuestion question;

    @Column(nullable = false)
    @Builder.Default
    private Integer orderIndex = 1;
}
