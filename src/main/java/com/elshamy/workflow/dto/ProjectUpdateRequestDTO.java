package com.elshamy.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectUpdateRequestDTO(
        @NotBlank(message = "Name is required")
        @Size(
                min = 4,
                max = 30,
                message = "Name must be between 4 and 30 characters"
        )
        String name,

        @NotBlank(message = "Description is required")
        @Size(
                max = 500,
                message = "Description must not exceed 500 characters"
        )
        String description
) {}