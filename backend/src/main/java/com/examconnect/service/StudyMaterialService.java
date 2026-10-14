package com.examconnect.service;

import com.examconnect.dto.*;
import com.examconnect.entity.*;
import com.examconnect.exception.ResourceNotFoundException;
import com.examconnect.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StudyMaterialService {

    private final StudyMaterialRepository studyMaterialRepository;
    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;
    private final BookmarkRepository bookmarkRepository;
    private final MaterialProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Transactional(readOnly = true)
    public PagedResponse<StudyMaterialDto> getMaterials(
            Long examId, Long subjectId, Long topicId, String search,
            int page, int size, Long currentUserId) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<StudyMaterial> materialPage = studyMaterialRepository.searchMaterials(examId, subjectId, topicId, search, pageable);

        Page<StudyMaterialDto> dtoPage = materialPage.map(mat -> mapToDto(mat, currentUserId));

        return PagedResponse.<StudyMaterialDto>builder()
                .content(dtoPage.getContent())
                .page(dtoPage.getNumber())
                .size(dtoPage.getSize())
                .totalElements(dtoPage.getTotalElements())
                .totalPages(dtoPage.getTotalPages())
                .last(dtoPage.isLast())
                .build();
    }

    @Transactional
    public StudyMaterialDto getMaterialById(Long id, Long currentUserId) {
        StudyMaterial material = studyMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Study material not found with ID: " + id));

        // Track user access progress
        if (currentUserId != null) {
            userRepository.findById(currentUserId).ifPresent(user -> {
                MaterialProgress progress = progressRepository.findByUserIdAndMaterialId(currentUserId, id)
                        .orElse(MaterialProgress.builder().user(user).material(material).build());
                progress.setLastAccessedAt(LocalDateTime.now());
                progressRepository.save(progress);
            });
        }

        return mapToDto(material, currentUserId);
    }

    @Transactional
    public boolean toggleBookmark(Long materialId, Long userId) {
        StudyMaterial material = studyMaterialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Study material not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (bookmarkRepository.existsByUserIdAndMaterialId(userId, materialId)) {
            bookmarkRepository.deleteByUserIdAndMaterialId(userId, materialId);
            return false;
        } else {
            Bookmark bookmark = Bookmark.builder().user(user).material(material).build();
            bookmarkRepository.save(bookmark);
            return true;
        }
    }

    @Transactional
    public void removeBookmark(Long materialId, Long userId) {
        bookmarkRepository.deleteByUserIdAndMaterialId(userId, materialId);
    }

    @Transactional(readOnly = true)
    public PagedResponse<BookmarkDto> getUserBookmarks(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Bookmark> bookmarks = bookmarkRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);

        Page<BookmarkDto> dtoPage = bookmarks.map(b -> BookmarkDto.builder()
                .id(b.getId())
                .materialId(b.getMaterial().getId())
                .title(b.getMaterial().getTitle())
                .materialType(b.getMaterial().getMaterialType())
                .examName(b.getMaterial().getExam().getName())
                .subjectName(b.getMaterial().getSubject().getName())
                .topicName(b.getMaterial().getTopic() != null ? b.getMaterial().getTopic().getName() : null)
                .bookmarkedAt(b.getCreatedAt())
                .build());

        return PagedResponse.<BookmarkDto>builder()
                .content(dtoPage.getContent())
                .page(dtoPage.getNumber())
                .size(dtoPage.getSize())
                .totalElements(dtoPage.getTotalElements())
                .totalPages(dtoPage.getTotalPages())
                .last(dtoPage.isLast())
                .build();
    }

    @Transactional
    public void markProgress(Long materialId, Long userId, boolean completed) {
        StudyMaterial material = studyMaterialRepository.findById(materialId)
                .orElseThrow(() -> new ResourceNotFoundException("Study material not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        MaterialProgress progress = progressRepository.findByUserIdAndMaterialId(userId, materialId)
                .orElse(MaterialProgress.builder().user(user).material(material).build());

        progress.setCompleted(completed);
        progress.setLastAccessedAt(LocalDateTime.now());
        progressRepository.save(progress);
    }

    // Admin methods
    @Transactional
    public StudyMaterialDto createMaterial(StudyMaterialRequest request, MultipartFile file, Long adminId) {
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        Topic topic = null;
        if (request.getTopicId() != null) {
            topic = topicRepository.findById(request.getTopicId()).orElse(null);
        }
        User admin = userRepository.findById(adminId).orElse(null);

        StudyMaterial material = StudyMaterial.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .materialType(request.getMaterialType())
                .contentText(request.getContentText())
                .exam(exam)
                .subject(subject)
                .topic(topic)
                .createdBy(admin)
                .active(request.isActive())
                .build();

        if (file != null && !file.isEmpty()) {
            String fileUrl = fileStorageService.storeFile(file, "materials");
            material.setFileUrl(fileUrl);
            material.setFileName(file.getOriginalFilename());
            material.setFileSize(file.getSize());
            if (material.getMaterialType() == null || material.getMaterialType().isEmpty()) {
                material.setMaterialType("PDF");
            }
        }

        return mapToDto(studyMaterialRepository.save(material), adminId);
    }

    @Transactional
    public StudyMaterialDto updateMaterial(Long id, StudyMaterialRequest request, MultipartFile file, Long adminId) {
        StudyMaterial material = studyMaterialRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Study material not found with ID: " + id));

        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        Topic topic = null;
        if (request.getTopicId() != null) {
            topic = topicRepository.findById(request.getTopicId()).orElse(null);
        }

        material.setTitle(request.getTitle());
        material.setDescription(request.getDescription());
        material.setMaterialType(request.getMaterialType());
        material.setContentText(request.getContentText());
        material.setExam(exam);
        material.setSubject(subject);
        material.setTopic(topic);
        material.setActive(request.isActive());

        if (file != null && !file.isEmpty()) {
            String fileUrl = fileStorageService.storeFile(file, "materials");
            material.setFileUrl(fileUrl);
            material.setFileName(file.getOriginalFilename());
            material.setFileSize(file.getSize());
        }

        return mapToDto(studyMaterialRepository.save(material), adminId);
    }

    @Transactional
    public void deleteMaterial(Long id) {
        if (!studyMaterialRepository.existsById(id)) {
            throw new ResourceNotFoundException("Study material not found with ID: " + id);
        }
        studyMaterialRepository.deleteById(id);
    }

    public StudyMaterialDto mapToDto(StudyMaterial mat, Long currentUserId) {
        boolean bookmarked = false;
        boolean completed = false;

        if (currentUserId != null) {
            bookmarked = bookmarkRepository.existsByUserIdAndMaterialId(currentUserId, mat.getId());
            completed = progressRepository.findByUserIdAndMaterialId(currentUserId, mat.getId())
                    .map(MaterialProgress::isCompleted)
                    .orElse(false);
        }

        return StudyMaterialDto.builder()
                .id(mat.getId())
                .title(mat.getTitle())
                .description(mat.getDescription())
                .materialType(mat.getMaterialType())
                .fileUrl(mat.getFileUrl())
                .fileName(mat.getFileName())
                .fileSize(mat.getFileSize())
                .contentText(mat.getContentText())
                .examId(mat.getExam().getId())
                .examName(mat.getExam().getName())
                .subjectId(mat.getSubject().getId())
                .subjectName(mat.getSubject().getName())
                .topicId(mat.getTopic() != null ? mat.getTopic().getId() : null)
                .topicName(mat.getTopic() != null ? mat.getTopic().getName() : null)
                .active(mat.isActive())
                .bookmarked(bookmarked)
                .completed(completed)
                .createdAt(mat.getCreatedAt())
                .build();
    }
}
