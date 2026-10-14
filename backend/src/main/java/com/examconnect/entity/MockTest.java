package com.examconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mock_tests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MockTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @Column(nullable = false)
    @Builder.Default
    private Integer durationMinutes = 60;

    @Column(nullable = false)
    @Builder.Default
    private Integer totalQuestions = 20;

    @Column(nullable = false)
    @Builder.Default
    private Double totalMarks = 20.0;

    @Column(nullable = false)
    @Builder.Default
    private Double passingMarks = 8.0;

    @Column(nullable = false)
    @Builder.Default
    private Double negativeMarkingPerQuestion = 0.25;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @OneToMany(mappedBy = "mockTest", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @Builder.Default
    @ToString.Exclude
    private List<MockTestQuestion> testQuestions = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
