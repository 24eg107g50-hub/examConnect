package com.examconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doubts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Doubt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id")
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Column(length = 500)
    private String attachmentUrl;

    @Column(nullable = false)
    @Builder.Default
    private boolean hasAcceptedAnswer = false;

    private Long acceptedAnswerId;

    @Column(nullable = false)
    @Builder.Default
    private Integer answersCount = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer viewsCount = 0;

    @OneToMany(mappedBy = "doubt", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("isAccepted DESC, upvotesCount DESC, createdAt DESC")
    @Builder.Default
    @ToString.Exclude
    private List<Answer> answers = new ArrayList<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
