package com.examconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_practice_attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPracticeAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private PracticeQuestion question;

    @Column(nullable = false, length = 10)
    private String selectedOption;

    @Column(nullable = false)
    private boolean isCorrect;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
