package com.elshamy.workflow.dto;

import com.elshamy.workflow.enums.TaskPriority;
import com.elshamy.workflow.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record TaskUpdateRequestDTO(
        @Size(min = 4, max = 150, message = "Title must be between 4 and 150 characters")
        String title,

        @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description,

        TaskStatus status,

        TaskPriority priority,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @FutureOrPresent(message = "Due date must be in the present or future")
        LocalDateTime dueDate
) {}