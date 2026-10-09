package com.elshamy.workflow.service;

import com.elshamy.workflow.dto.ProjectRequestDTO;
import com.elshamy.workflow.dto.ProjectResponseDTO;
import com.elshamy.workflow.dto.ProjectUpdateRequestDTO;
import com.elshamy.workflow.entity.Project;
import com.elshamy.workflow.entity.User;
import com.elshamy.workflow.exception.ResourceNotFoundException;
import com.elshamy.workflow.repository.ProjectRepository;
import com.elshamy.workflow.security.CurrentUserService;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final CurrentUserService currentUserService;

    public ProjectService(
            ProjectRepository projectRepository,
            CurrentUserService currentUserService) {
        this.projectRepository = projectRepository;
        this.currentUserService = currentUserService;
    }

    public ProjectResponseDTO createProject(ProjectRequestDTO projectRequestDTO){
        User owner = currentUserService.getCurrentUser();
        Project project = new Project(
                projectRequestDTO.name(),
                projectRequestDTO.description()
        );
        project.setOwner(owner);
        Project projectSaved = projectRepository.save(project);
        return toDTO(projectSaved);
    }

    private ProjectResponseDTO toDTO(Project project){
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

    public List<ProjectResponseDTO> getAllProjects(){
        return projectRepository.findAll().stream().map(this::toDTO).toList();
    }

    public ProjectResponseDTO getProjectById(Long id){
        Project project = projectRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Project not found with id: " + id)
                );

        return toDTO(project);
    }

    public ProjectResponseDTO updateProject(ProjectUpdateRequestDTO request, Long id){
        Project project = projectRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("project not found with id: " + id)
        );

        project.setName(request.name());
        project.setDescription(request.description());
        Project projectSaved = projectRepository.save(project);
        return toDTO(projectSaved);
    }

    public void deleteProject(Long id){
        if(! projectRepository.existsById(id)){
            throw new ResourceNotFoundException("Project not found with id: " + id);
        }
        projectRepository.deleteById(id);
    }


}