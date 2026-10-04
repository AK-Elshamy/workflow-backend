package com.elshamy.workflow.service;

import com.elshamy.workflow.entity.Project;
import com.elshamy.workflow.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository){
        this.projectRepository =  projectRepository;
    }

    public List<Project> getAllProject(){
        return projectRepository.findAll();
    }

    public Optional<Project> findById(Long id){
        return projectRepository.findById(id);
    }
}