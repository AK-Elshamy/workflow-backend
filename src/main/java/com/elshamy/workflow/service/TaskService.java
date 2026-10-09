package com.elshamy.workflow.service;

import com.elshamy.workflow.dto.TaskRequestDTO;
import com.elshamy.workflow.dto.TaskResponseDTO;
import com.elshamy.workflow.dto.TaskUpdateRequestDTO;
import com.elshamy.workflow.entity.Project;
import com.elshamy.workflow.entity.Task;
import com.elshamy.workflow.entity.User;
import com.elshamy.workflow.enums.Role;
import com.elshamy.workflow.exception.AccessDeniedException;
import com.elshamy.workflow.exception.ResourceNotFoundException;
import com.elshamy.workflow.repository.ProjectRepository;
import com.elshamy.workflow.repository.TaskRepository;
import com.elshamy.workflow.repository.UserRepository;
import com.elshamy.workflow.security.CurrentUserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final CurrentUserService currentUserService;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository, CurrentUserService currentUserService, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.currentUserService = currentUserService;
        this.userRepository = userRepository;
    }

    private boolean isAdmin(User user){
        return user.getRole() == Role.ADMIN;
    }

    private boolean isProjectOwner(User user, Task task){
        return task.getProject().getOwner().getId().equals(user.getId());
    }

    private boolean canAccessProject(User user, Project project) {
        if (isAdmin(user)) {
            return true;
        }
        boolean isOwner = project.getOwner().getId().equals(user.getId());
        boolean isMember = project.getMembers().stream()
                .anyMatch(member -> member.getId().equals(user.getId()));

        return isOwner || isMember;
    }

    public TaskResponseDTO createTask(TaskRequestDTO taskRequestDTO) {
        Project project = projectRepository.findById(taskRequestDTO.projectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + taskRequestDTO.projectId()));

        User currentUser = currentUserService.getCurrentUser();

        if (!canAccessProject(currentUser, project)) {
            throw new AccessDeniedException("You do not have access to create tasks in this project");
        }

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
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        User currentUser = currentUserService.getCurrentUser();

        if (!canAccessProject(currentUser, task.getProject())) {
            throw new AccessDeniedException("You do not have access to view this task");
        }

        return toDTO(task);
    }

    public List<TaskResponseDTO> getProjectTasks(Long projectId){
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        User currentUser = currentUserService.getCurrentUser();

        if (!canAccessProject(currentUser, project)) {
            throw new AccessDeniedException("You do not have access to view tasks for this project");
        }

        return project.getTasks().stream().map(this::toDTO).toList();
    }

    public List<TaskResponseDTO> getUserTasks(Long userId){
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found with id: " + userId));

        User currentUser = currentUserService.getCurrentUser();

        if (!isAdmin(currentUser) && !userId.equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only view your own assigned tasks");
        }

        return user.getAssignedTasks().stream().map(this::toDTO).toList();
    }

    public TaskResponseDTO assignTask(Long taskId, Long userId){
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        User currentUser = currentUserService.getCurrentUser();

        if(!isProjectOwner(currentUser, task) && !isAdmin(currentUser)){
            throw new AccessDeniedException("Cannot assign task. You must be Admin or Project Owner");
        }

        User userToAssign = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException(String.format("User with id %d not found", userId))
        );


        Project project = task.getProject();
        boolean targetIsOwner = project.getOwner().getId().equals(userId);
        boolean targetIsMember = project.getMembers().stream().anyMatch(m -> m.getId().equals(userId));

        if (!targetIsOwner && !targetIsMember) {
            throw new AccessDeniedException("Cannot assign task to a user who is not a member of this project");
        }

        task.setAssignee(userToAssign);
        Task taskSaved = taskRepository.save(task);
        return toDTO(taskSaved);
    }

    public TaskResponseDTO updateTask(Long taskId, TaskUpdateRequestDTO taskUpdateRequestDTO) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        User currentUser = currentUserService.getCurrentUser();

        if (!canAccessProject(currentUser, task.getProject())) {
            throw new AccessDeniedException("You do not have permission to update this task");
        }

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
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
        User currentUser = currentUserService.getCurrentUser();

        if(!isProjectOwner(currentUser, task) && !isAdmin(currentUser)){
            throw new AccessDeniedException("Cannot delete task. You must be Admin or Project Owner");
        }

        taskRepository.delete(task);
    }
}