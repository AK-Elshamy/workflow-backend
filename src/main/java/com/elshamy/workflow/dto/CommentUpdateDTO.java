package com.elshamy.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentUpdateDTO(
        @NotBlank(message = "Comment content cannot be empty")
        @Size(max = 1000, message = "Comment content must not exceed 1000 characters")
        String content
) {}