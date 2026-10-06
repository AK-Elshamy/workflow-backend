package com.elshamy.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectRequestDTO(
        @NotBlank(message = "Name is required")
        @Size(max = 30, message = "Name must not exceed 30 characters")
        String name,

        @NotBlank(message = "Description is required")
        @Size(max = 250, message = "Description must not exceed 250 characters")
        String description
) {}