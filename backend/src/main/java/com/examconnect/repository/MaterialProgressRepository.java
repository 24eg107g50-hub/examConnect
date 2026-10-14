package com.examconnect.repository;

import com.examconnect.entity.MaterialProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterialProgressRepository extends JpaRepository<MaterialProgress, Long> {
    Optional<MaterialProgress> findByUserIdAndMaterialId(Long userId, Long materialId);
    long countByUserId(Long userId);
    long countByUserIdAndCompletedTrue(Long userId);

    @Query("SELECT mp FROM MaterialProgress mp WHERE mp.user.id = :userId ORDER BY mp.lastAccessedAt DESC")
    List<MaterialProgress> findRecentlyAccessed(@Param("userId") Long userId);
}
