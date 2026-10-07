package com.elshamy.workflow.service;


import com.elshamy.workflow.dto.TaskRequestDTO;
import com.elshamy.workflow.dto.TaskResponseDTO;
import com.elshamy.workflow.dto.TaskUpdateRequestDTO;
import com.elshamy.workflow.entity.Project;
import com.elshamy.workflow.entity.Task;
import com.elshamy.workflow.entity.User;
import com.elshamy.workflow.exception.AccessDeniedException;
import com.elshamy.workflow.exception.ResourceNotFoundException;
import com.elshamy.workflow.repository.ProjectRepository;
import com.elshamy.workflow.repository.TaskRepository;
import com.elshamy.workflow.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public TaskResponseDTO createTask(TaskRequestDTO taskRequestDTO) {
        Project project = projectRepository.findById(taskRequestDTO.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + taskRequestDTO.projectId()));

        Task task = new Task(
                taskRequestDTO.title(),
                taskRequestDTO.description(),
                taskRequestDTO.status(),
                taskRequestDTO.priority()
        );

        task.setProject(project);
        task.setDueDate(taskRequestDTO.dueDate());

        Task taskSaved = taskRepository.save(task);
        return toDTO(taskSaved);
    }

    private TaskResponseDTO toDTO(Task task) {
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getStatus(),
                task.getPriority(),
                task.getDueDate(),
                task.getProject().getId(),
                task.getProject().getName(),
                task.getAssignee() != null ? task.getAssignee().getId() : null,
                task.getAssignee() != null ? task.getAssignee().getUsername() : null,
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }

    public TaskResponseDTO getTask(Long id){
        Task task = taskRepository.findById(id).orElseThrow(()
        -> new ResourceNotFoundException("Task not found with id: " + id));
        return toDTO(task);
    }

    public List<TaskResponseDTO> getProjectTasks(Long projectId){
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        return project.getTasks().stream().map(this::toDTO).toList();
    }
    public List<TaskResponseDTO> getUserTasks(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        return user.getAssignedTasks().stream().map(this::toDTO).toList();
    }


    public TaskResponseDTO assignTask(Long taskId, Long userId){

        Task task = taskRepository.findById(taskId).orElseThrow(()
                -> new ResourceNotFoundException("Task not found with id: " + taskId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Project project = task.getProject();
        boolean isOwner = project.getOwner().getId().equals(user.getId());
        boolean isMember = user.getMemberProjects().contains(project);

        if (!isOwner && !isMember) {
            throw new AccessDeniedException("User is neither the owner nor a member of this project");
        }

        task.setAssignee(user);

        Task taskSaved = taskRepository.save(task);
        return toDTO(taskSaved);

    }


    public TaskResponseDTO updateTask(Long taskId, TaskUpdateRequestDTO taskUpdateRequestDTO) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        if (taskUpdateRequestDTO.title() != null && !taskUpdateRequestDTO.title().isBlank()) {
            task.setTitle(taskUpdateRequestDTO.title());
        }

        if (taskUpdateRequestDTO.description() != null) {
            task.setDescription(taskUpdateRequestDTO.description());
        }

        if (taskUpdateRequestDTO.status() != null) {
            task.setStatus(taskUpdateRequestDTO.status());
        }

        if (taskUpdateRequestDTO.priority() != null) {
            task.setPriority(taskUpdateRequestDTO.priority());
        }

        if (taskUpdateRequestDTO.dueDate() != null) {
            task.setDueDate(taskUpdateRequestDTO.dueDate());
        }

        Task updatedTask = taskRepository.save(task);
        return toDTO(updatedTask);
    }

    public void deleteTask(Long taskId){
        Task task = taskRepository.findById(taskId).orElseThrow(()
                -> new ResourceNotFoundException("Task not found with id: " + taskId));
        taskRepository.delete(task);
    }
}