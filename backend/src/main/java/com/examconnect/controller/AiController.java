package com.examconnect.controller;

import com.examconnect.ai.RagService;
import com.examconnect.dto.*;
import com.examconnect.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final RagService ragService;

    @PostMapping("/chat")
    public ResponseEntity<ApiResponse<ChatResponse>> chat(
            @Valid @RequestBody ChatRequest request,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        ChatResponse response = ragService.processChat(request, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse<List<ChatSessionDto>>> getSessions(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        List<ChatSessionDto> sessions = ragService.getUserSessions(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(sessions));
    }

    @GetMapping("/sessions/{id}/messages")
    public ResponseEntity<ApiResponse<List<ChatMessageDto>>> getSessionMessages(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        List<ChatMessageDto> messages = ragService.getSessionMessages(id, userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success(messages));
    }
}
