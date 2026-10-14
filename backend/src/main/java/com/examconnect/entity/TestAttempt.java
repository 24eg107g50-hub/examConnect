package com.examconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "test_attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mock_test_id", nullable = false)
    private MockTest mockTest;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "IN_PROGRESS"; // IN_PROGRESS, COMPLETED, TIMED_OUT

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime startTime;

    private LocalDateTime submitTime;

    @Builder.Default
    private Integer timeTakenSeconds = 0;

    @Builder.Default
    private Double totalScore = 0.0;

    @Builder.Default
    private Double maxScore = 0.0;

    @Builder.Default
    private Double percentage = 0.0;

    @Builder.Default
    private Integer correctCount = 0;

    @Builder.Default
    private Integer incorrectCount = 0;

    @Builder.Default
    private Integer unansweredCount = 0;

    @Builder.Default
    private Double accuracy = 0.0;

    @Builder.Default
    private Boolean passed = false;

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    private List<TestResponse> responses = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
