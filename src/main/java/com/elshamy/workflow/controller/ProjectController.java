package com.elshamy.workflow.controller;

import com.elshamy.workflow.dto.ProjectRequestDTO;
import com.elshamy.workflow.dto.ProjectResponseDTO;
import com.elshamy.workflow.dto.ProjectUpdateRequestDTO;
import com.elshamy.workflow.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<ProjectResponseDTO>> getAllProjects(){
        return ResponseEntity.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> getProjectById(@PathVariable("id") Long id){
        ProjectResponseDTO project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponseDTO> updateProject(@PathVariable("id") Long id, @Valid @RequestBody ProjectUpdateRequestDTO projectUpdateRequestDTO) {
        ProjectResponseDTO response = projectService.updateProject(projectUpdateRequestDTO, id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable("id") Long id){
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }


}