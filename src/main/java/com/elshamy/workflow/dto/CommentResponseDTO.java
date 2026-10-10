package com.elshamy.workflow.dto;

import java.time.LocalDateTime;

public record CommentResponseDTO(
        Long id,
        String content,
        Long taskId,
        Long authorId,
        String authorUsername,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}