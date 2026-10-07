package com.elshamy.workflow.dto;
import jakarta.validation.constraints.NotNull;

public record AssignTaskRequestDTO(
        @NotNull
        Long userId
) {}