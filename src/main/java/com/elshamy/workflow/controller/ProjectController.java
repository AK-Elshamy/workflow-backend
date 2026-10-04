package com.elshamy.workflow.controller;

import com.elshamy.workflow.entity.Project;
import com.elshamy.workflow.service.ProjectService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ProjectController {
    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/projects")
    public List<Project> getAllProject(){
       return projectService.getAllProject();
    }
    @GetMapping("/projects/{id}")
    public ResponseEntity<Project> findById(@PathVariable("id") Long id){
        Optional<Project> project = projectService.findById(id);
        if(project.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(project.get());
    }
}