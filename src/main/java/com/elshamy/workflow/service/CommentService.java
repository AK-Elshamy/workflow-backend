package com.elshamy.workflow.service;

import com.elshamy.workflow.dto.CommentRequestDTO;
import com.elshamy.workflow.dto.CommentResponseDTO;
import com.elshamy.workflow.dto.CommentUpdateDTO;
import com.elshamy.workflow.entity.Comment;
import com.elshamy.workflow.entity.Project;
import com.elshamy.workflow.entity.Task;
import com.elshamy.workflow.entity.User;
import com.elshamy.workflow.enums.Role;
import com.elshamy.workflow.exception.AccessDeniedException;
import com.elshamy.workflow.exception.ResourceNotFoundException;
import com.elshamy.workflow.repository.CommentRepository;
import com.elshamy.workflow.repository.TaskRepository;
import com.elshamy.workflow.security.CurrentUserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final CurrentUserService currentUserService;

    public CommentService(CommentRepository commentRepository, TaskRepository taskRepository, CurrentUserService currentUserService) {
        this.commentRepository = commentRepository;
        this.taskRepository = taskRepository;
        this.currentUserService = currentUserService;
    }

    private boolean canAccessTaskProject(User user, Task task) {
        if (user.getRole() == Role.ADMIN) {
            return true;
        }

        Project project = task.getProject();
        boolean isOwner = project.getOwner().getId().equals(user.getId());
        boolean isMember = project.getMembers().stream()
                .anyMatch(member -> member.getId().equals(user.getId()));

        return isOwner || isMember;
    }


    private Task getTaskWithAccessCheck(Long taskId, User user, String deniedMessage) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        if (!canAccessTaskProject(user, task)) {
            throw new AccessDeniedException(deniedMessage);
        }
        return task;
    }

    @Transactional
    public CommentResponseDTO addComment(Long taskId, CommentRequestDTO commentRequestDTO) {
        User currentUser = currentUserService.getCurrentUser();
        Task task = getTaskWithAccessCheck(taskId, currentUser, "You do not have permission to comment on this task");

        Comment comment = new Comment();
        comment.setContent(commentRequestDTO.content());
        comment.setTask(task);
        comment.setAuthor(currentUser);

        Comment savedComment = commentRepository.save(comment);

        return toDTO(savedComment);
    }

    public List<CommentResponseDTO> getComments(Long taskId) {
        User currentUser = currentUserService.getCurrentUser();
        getTaskWithAccessCheck(taskId, currentUser, "You do not have permission to view comments on this task");

        return commentRepository.findByTaskId(taskId).stream()
                .map(this::toDTO)
                .toList();
    }

    public CommentResponseDTO updateComment(Long commentId, CommentUpdateDTO commentUpdateDTO) {
        User user = currentUserService.getCurrentUser();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));


        boolean isAdmin = user.getRole() == Role.ADMIN;
        boolean isAuthor = comment.getAuthor().getId().equals(user.getId());

        if (!isAdmin && !isAuthor) {
            throw new AccessDeniedException("You do not have permission to edit this comment");
        }

        comment.setContent(commentUpdateDTO.content());
        Comment commentSaved = commentRepository.save(comment);

        return toDTO(commentSaved);
    }

    private CommentResponseDTO toDTO(Comment comment) {
        return new CommentResponseDTO(
                comment.getId(),
                comment.getContent(),
                comment.getTask().getId(),
                comment.getAuthor().getId(),
                comment.getAuthor().getUsername(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }

    @Transactional
    public void deleteComment(Long commentId){
        User user = currentUserService.getCurrentUser();
        Comment comment = commentRepository.findById(commentId).orElseThrow(
                () -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        boolean isAdmin = user.getRole() == Role.ADMIN;
        boolean isAuthor = comment.getAuthor().getId().equals(user.getId());


        if (!isAdmin && !isAuthor) {
            throw new AccessDeniedException("You do not have permission to delete this comment");
        }

        commentRepository.delete(comment);
    }
}