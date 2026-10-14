package com.examconnect.repository;

import com.examconnect.entity.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Optional<Bookmark> findByUserIdAndMaterialId(Long userId, Long materialId);
    boolean existsByUserIdAndMaterialId(Long userId, Long materialId);
    void deleteByUserIdAndMaterialId(Long userId, Long materialId);
    Page<Bookmark> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    long countByUserId(Long userId);
}
