package com.examconnect.controller;

import com.examconnect.dto.ApiResponse;
import com.examconnect.dto.BookmarkDto;
import com.examconnect.dto.PagedResponse;
import com.examconnect.dto.StudyMaterialDto;
import com.examconnect.security.UserPrincipal;
import com.examconnect.service.StudyMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class StudyMaterialController {

    private final StudyMaterialService studyMaterialService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<StudyMaterialDto>>> getMaterials(
            @RequestParam(required = false) Long examId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Long userId = userPrincipal != null ? userPrincipal.getId() : null;
        PagedResponse<StudyMaterialDto> materials = studyMaterialService.getMaterials(examId, subjectId, topicId, search, page, size, userId);
        return ResponseEntity.ok(ApiResponse.success(materials));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudyMaterialDto>> getMaterialById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        Long userId = userPrincipal != null ? userPrincipal.getId() : null;
        StudyMaterialDto material = studyMaterialService.getMaterialById(id, userId);
        return ResponseEntity.ok(ApiResponse.success(material));
    }

    @PostMapping("/{id}/bookmark")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> toggleBookmark(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        boolean bookmarked = studyMaterialService.toggleBookmark(id, userPrincipal.getId());
        String msg = bookmarked ? "Material bookmarked successfully" : "Bookmark removed";
        return ResponseEntity.ok(ApiResponse.success(msg, Map.of("bookmarked", bookmarked)));
    }

    @DeleteMapping("/{id}/bookmark")
    public ResponseEntity<ApiResponse<String>> removeBookmark(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        studyMaterialService.removeBookmark(id, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("Bookmark removed successfully", null));
    }

    @GetMapping("/bookmarks")
    public ResponseEntity<ApiResponse<PagedResponse<BookmarkDto>>> getUserBookmarks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        PagedResponse<BookmarkDto> bookmarks = studyMaterialService.getUserBookmarks(userPrincipal.getId(), page, size);
        return ResponseEntity.ok(ApiResponse.success(bookmarks));
    }

    @PostMapping("/{id}/progress")
    public ResponseEntity<ApiResponse<String>> markProgress(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> payload,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        boolean completed = payload != null && Boolean.TRUE.equals(payload.get("completed"));
        studyMaterialService.markProgress(id, userPrincipal.getId(), completed);
        return ResponseEntity.ok(ApiResponse.success("Progress updated successfully", null));
    }
}
