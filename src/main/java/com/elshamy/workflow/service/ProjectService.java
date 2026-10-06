package com.elshamy.workflow.service;

import com.elshamy.workflow.dto.ProjectRequestDTO;
import com.elshamy.workflow.dto.ProjectResponseDTO;
import com.elshamy.workflow.entity.Project;
import com.elshamy.workflow.entity.User;
import com.elshamy.workflow.repository.ProjectRepository;
import com.elshamy.workflow.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public ProjectService(ProjectRepository projectRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public ProjectResponseDTO createProject(ProjectRequestDTO projectRequestDTO){
        Project project = new Project(projectRequestDTO.name(), projectRequestDTO.description());
        User owner = userRepository.findById(1L).orElseThrow(() -> new RuntimeException("Owner not found"));
        project.setOwner(owner);
        Project projectSaved = projectRepository.save(project);
        return toDTO(projectSaved);
    }
    public ProjectResponseDTO toDTO(Project project){
        return new ProjectResponseDTO(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getOwner().getId(),
                project.getOwner().getUsername(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}