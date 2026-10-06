package com.elshamy.workflow.controller;

import com.elshamy.workflow.dto.ProjectRequestDTO;
import com.elshamy.workflow.dto.ProjectResponseDTO;
import com.elshamy.workflow.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ResponseEntity<ProjectResponseDTO> createProject(@Valid @RequestBody ProjectRequestDTO projectRequestDTO) {
        ProjectResponseDTO projectResponseDTO = projectService.createProject(projectRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(projectResponseDTO);
    }
}