package com.examconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "material_progress", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "material_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id", nullable = false)
    private StudyMaterial material;

    @Column(nullable = false)
    @Builder.Default
    private boolean completed = false;

    @UpdateTimestamp
    private LocalDateTime lastAccessedAt;
}
