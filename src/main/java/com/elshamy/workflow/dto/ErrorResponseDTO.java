package com.elshamy.workflow.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponseDTO(
        int status,
        String message,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp,

        String path,
        Map<String, String> errors
) {
    public ErrorResponseDTO(int status, String message, LocalDateTime timestamp, String path) {
        this(status, message, timestamp, path, null);
    }
}