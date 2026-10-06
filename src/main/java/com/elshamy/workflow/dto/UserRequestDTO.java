package com.elshamy.workflow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequestDTO(
        @NotBlank(message = "User name required")
        @Size(min = 4, max = 15, message = "Username length between [4, 15]")
        String username,

        @Email(message = "write valid email")
        @NotBlank(message = "email required")
        String email,

        @NotBlank(message = "password required")
        @Size(min = 8, max = 100, message = "password not valid")
        String password
) {}