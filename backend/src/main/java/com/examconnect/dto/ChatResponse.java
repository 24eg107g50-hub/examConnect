package com.examconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private Long sessionId;
    private String response;
    private List<SourceCitationDto> citations;
    private boolean groundedInDocuments;
    private String modelUsed;
    private boolean fallbackMode;
    private String notice;
}
