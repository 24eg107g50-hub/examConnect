package com.examconnect.repository;

import com.examconnect.entity.StudyMaterial;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudyMaterialRepository extends JpaRepository<StudyMaterial, Long> {

    @Query("SELECT m FROM StudyMaterial m WHERE m.active = true " +
           "AND (:examId IS NULL OR m.exam.id = :examId) " +
           "AND (:subjectId IS NULL OR m.subject.id = :subjectId) " +
           "AND (:topicId IS NULL OR m.topic.id = :topicId) " +
           "AND (:search IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(m.description) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<StudyMaterial> searchMaterials(
            @Param("examId") Long examId,
            @Param("subjectId") Long subjectId,
            @Param("topicId") Long topicId,
            @Param("search") String search,
            Pageable pageable
    );

    List<StudyMaterial> findTop5ByActiveTrueOrderByCreatedAtDesc();
    long countByActiveTrue();
}
