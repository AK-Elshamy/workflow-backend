package com.elshamy.workflow.dto;

import com.elshamy.workflow.enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record UserResponseDTO(
        Long id,
        String username,
        String email,
        Role role,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime createdAt,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime updatedAt
) {}