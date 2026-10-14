package com.examconnect.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String role;
    private boolean enabled;
    private LocalDateTime createdAt;
}
