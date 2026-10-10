package com.elshamy.workflow.dto;

import com.elshamy.workflow.enums.Role;
import jakarta.validation.constraints.*;

public record UserUpdateDTO(

        @Size(min = 4, max = 15, message = "Username must be between 4 and 15 characters")
        @Pattern(regexp = "^[a-zA-Z0-9_.-]+$", message = "Username can only contain alphanumeric characters, underscores, and hyphens")
        String username,

        @Email(message = "Please provide a valid email address")
        String email,

        @Size(min = 8, max = 100, message = "Password must be at least 8 characters long")
        String password,

        Role role
) {}