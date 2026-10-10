package com.elshamy.workflow.service;

import com.elshamy.workflow.dto.CommentRequestDTO;
import com.elshamy.workflow.dto.CommentResponseDTO;
import com.elshamy.workflow.dto.CommentUpdateDTO;
import com.elshamy.workflow.entity.Comment;
import com.elshamy.workflow.entity.Project;
import com.elshamy.workflow.entity.Task;
import com.elshamy.workflow.entity.User;
import com.elshamy.workflow.enums.Role;
import com.elshamy.workflow.enums.TaskPriority;
import com.elshamy.workflow.enums.TaskStatus;
import com.elshamy.workflow.exception.AccessDeniedException;
import com.elshamy.workflow.exception.ResourceNotFoundException;
import com.elshamy.workflow.repository.CommentRepository;
import com.elshamy.workflow.repository.TaskRepository;
import com.elshamy.workflow.security.CurrentUserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private CommentService commentService;

    private User user;
    private Project project;
    private Task task;

    @BeforeEach
    void setUp() {
        user = new User(
                "testuser",
                "test@example.com",
                "encoded-password",
                Role.USER
        );
        user.setId(1L); // Setting a dummy ID for the user

        project = new Project("Test Project", "Project for testing");
        project.setId(10L);
        project.setOwner(user);

        task = new Task(
                "Test Task",
                "Task for testing",
                TaskStatus.TODO,
                TaskPriority.MEDIUM
        );
        task.setId(20L);
        task.setProject(project);
    }

    @Test
    void addComment_WhenUserOwnsProject_ShouldSaveAndReturnComment() {
        // Arrange: Set up the expected data and mock behaviors
        String content = "This is a test comment";
        CommentRequestDTO request = new CommentRequestDTO(content);

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findById(20L)).thenReturn(Optional.of(task));

        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            comment.setId(30L);
            return comment;
        });

        // Act: Execute the method under test
        CommentResponseDTO response = commentService.addComment(20L, request);

        // Assert: Verify the results
        assertNotNull(response);
        assertEquals(30L, response.id());
        assertEquals(content, response.content());
        assertEquals(20L, response.taskId());
        assertEquals("testuser", response.authorUsername());
        assertEquals(user.getId(), response.authorId());

        // Verify: Ensure the save method was called exactly once
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void addComment_WhenTaskDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Arrange: Mock the behavior when a task is not found
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        CommentRequestDTO request = new CommentRequestDTO("Test comment");

        // Act & Assert: Verify that the service throws the expected exception
        assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.addComment(999L, request)
        );

        // Verify: Ensure that no comment is saved in the database
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void addComment_WhenUserHasNoProjectAccess_ShouldThrowAccessDeniedException() {
        // Arrange
        User anotherUser = new User(
                "anotheruser",
                "another@example.com",
                "encoded-password",
                Role.USER
        );
        ReflectionTestUtils.setField(anotherUser, "id", 2L);

        when(currentUserService.getCurrentUser()).thenReturn(anotherUser);
        when(taskRepository.findById(20L)).thenReturn(Optional.of(task));

        CommentRequestDTO request =
                new CommentRequestDTO("Unauthorized comment");

        // Act & Assert
        assertThrows(
                AccessDeniedException.class,
                () -> commentService.addComment(20L, request)
        );

        // Verify
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void updateComment_WhenUserIsAuthor_ShouldUpdateAndReturnComment() {
        // Arrange
        Comment comment = new Comment();
        comment.setId(30L);
        comment.setContent("Old content");
        comment.setTask(task);
        comment.setAuthor(user);

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(commentRepository.findById(30L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CommentUpdateDTO request = new CommentUpdateDTO("Updated content");

        // Act
        CommentResponseDTO response =
                commentService.updateComment(30L, request);

        // Assert
        assertEquals("Updated content", response.content());
        assertEquals(30L, response.id());
        verify(commentRepository).save(comment);
    }


    @Test
    void updateComment_WhenUserIsNotAuthorOrAdmin_ShouldThrowAccessDeniedException() {
        // Arrange
        User anotherUser = new User(
                "anotheruser",
                "another@example.com",
                "encoded-password",
                Role.USER
        );
        ReflectionTestUtils.setField(anotherUser, "id", 2L);

        Comment comment = new Comment();
        comment.setId(30L);
        comment.setContent("Original content");
        comment.setTask(task);
        comment.setAuthor(user);

        when(currentUserService.getCurrentUser()).thenReturn(anotherUser);
        when(commentRepository.findById(30L)).thenReturn(Optional.of(comment));

        CommentUpdateDTO request = new CommentUpdateDTO("Unauthorized update");

        // Act & Assert
        assertThrows(
                AccessDeniedException.class,
                () -> commentService.updateComment(30L, request)
        );

        // Verify
        verify(commentRepository, never()).save(any(Comment.class));
    }


    @Test
    void deleteComment_WhenUserIsAuthor_ShouldDeleteComment() {
        // Arrange
        Comment comment = new Comment();
        comment.setId(30L);
        comment.setContent("Comment to delete");
        comment.setTask(task);
        comment.setAuthor(user);

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(commentRepository.findById(30L)).thenReturn(Optional.of(comment));

        // Act
        commentService.deleteComment(30L);

        // Assert
        verify(commentRepository).delete(comment);
    }



    @Test
    void deleteComment_WhenUserIsNotAuthorOrAdmin_ShouldThrowAccessDeniedException() {
        // Arrange
        User anotherUser = new User(
                "anotheruser",
                "another@example.com",
                "encoded-password",
                Role.USER
        );
        ReflectionTestUtils.setField(anotherUser, "id", 2L);

        Comment comment = new Comment();
        comment.setId(30L);
        comment.setContent("Another user's comment");
        comment.setTask(task);
        comment.setAuthor(user);

        when(currentUserService.getCurrentUser()).thenReturn(anotherUser);
        when(commentRepository.findById(30L)).thenReturn(Optional.of(comment));

        // Act & Assert
        assertThrows(
                AccessDeniedException.class,
                () -> commentService.deleteComment(30L)
        );

        // Verify
        verify(commentRepository, never()).delete(any(Comment.class));
    }


    @Test
    void getComments_WhenUserOwnsProject_ShouldReturnComments() {
        // Arrange
        Comment comment = new Comment();
        comment.setId(30L);
        comment.setContent("Test comment");
        comment.setTask(task);
        comment.setAuthor(user);

        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(taskRepository.findById(20L)).thenReturn(Optional.of(task));
        when(commentRepository.findByTaskId(20L)).thenReturn(List.of(comment));

        // Act
        List<CommentResponseDTO> responses = commentService.getComments(20L);

        // Assert
        assertEquals(1, responses.size());
        assertEquals("Test comment", responses.get(0).content());
        assertEquals(30L, responses.get(0).id());

        verify(commentRepository).findByTaskId(20L);
    }

    @Test
    void getComments_WhenUserHasNoProjectAccess_ShouldThrowAccessDeniedException() {
        // Arrange
        User anotherUser = new User(
                "anotheruser",
                "another@example.com",
                "encoded-password",
                Role.USER
        );
        ReflectionTestUtils.setField(anotherUser, "id", 2L);

        when(currentUserService.getCurrentUser()).thenReturn(anotherUser);
        when(taskRepository.findById(20L)).thenReturn(Optional.of(task));

        // Act & Assert
        assertThrows(
                AccessDeniedException.class,
                () -> commentService.getComments(20L)
        );

        // Verify: لا يجب جلب التعليقات بعد رفض الوصول
        verify(commentRepository, never()).findByTaskId(anyLong());
    }

    @Test
    void updateComment_WhenCommentDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        CommentUpdateDTO request =
                new CommentUpdateDTO("Updated content");

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.updateComment(999L, request)
        );

        // Verify: لا يجب حفظ أي تعديل
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void deleteComment_WhenCommentDoesNotExist_ShouldThrowResourceNotFoundException() {
        // Arrange
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(commentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> commentService.deleteComment(999L)
        );

        // Verify: لا يجب حذف أي تعليق
        verify(commentRepository, never()).delete(any(Comment.class));
    }


    @Test
    void updateComment_WhenUserIsAdmin_ShouldUpdateComment() {
        // Arrange
        User admin = new User(
                "admin",
                "admin@example.com",
                "encoded-password",
                Role.ADMIN
        );
        ReflectionTestUtils.setField(admin, "id", 3L);

        Comment comment = new Comment();
        comment.setId(30L);
        comment.setContent("Original content");
        comment.setTask(task);
        comment.setAuthor(user);

        when(currentUserService.getCurrentUser()).thenReturn(admin);
        when(commentRepository.findById(30L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CommentUpdateDTO request =
                new CommentUpdateDTO("Updated by admin");

        // Act
        CommentResponseDTO response =
                commentService.updateComment(30L, request);

        // Assert
        assertEquals("Updated by admin", response.content());
        verify(commentRepository).save(comment);
    }

    @Test
    void deleteComment_WhenUserIsAdmin_ShouldDeleteComment() {
        // Arrange
        User admin = new User(
                "admin",
                "admin@example.com",
                "encoded-password",
                Role.ADMIN
        );
        ReflectionTestUtils.setField(admin, "id", 3L);

        Comment comment = new Comment();
        comment.setId(30L);
        comment.setContent("Another user's comment");
        comment.setTask(task);
        comment.setAuthor(user);

        when(currentUserService.getCurrentUser()).thenReturn(admin);
        when(commentRepository.findById(30L)).thenReturn(Optional.of(comment));

        // Act
        commentService.deleteComment(30L);

        // Assert
        verify(commentRepository).delete(comment);
    }

    @Test
    void addComment_WhenUserIsProjectMember_ShouldSaveAndReturnComment() {
        // Arrange
        User member = new User(
                "member",
                "member@example.com",
                "encoded-password",
                Role.USER
        );
        ReflectionTestUtils.setField(member, "id", 2L);

        project.getMembers().add(member);

        when(currentUserService.getCurrentUser()).thenReturn(member);
        when(taskRepository.findById(20L)).thenReturn(Optional.of(task));

        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> {
                    Comment comment = invocation.getArgument(0);
                    comment.setId(31L);
                    return comment;
                });

        CommentRequestDTO request =
                new CommentRequestDTO("Comment from project member");

        // Act
        CommentResponseDTO response =
                commentService.addComment(20L, request);

        // Assert
        assertEquals(31L, response.id());
        assertEquals("Comment from project member", response.content());
        assertEquals(2L, response.authorId());

        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void addComment_WhenUserIsAdmin_ShouldSaveAndReturnComment() {
        // Arrange
        User admin = new User(
                "admin",
                "admin@example.com",
                "encoded-password",
                Role.ADMIN
        );
        ReflectionTestUtils.setField(admin, "id", 3L);

        when(currentUserService.getCurrentUser()).thenReturn(admin);
        when(taskRepository.findById(20L)).thenReturn(Optional.of(task));

        when(commentRepository.save(any(Comment.class)))
                .thenAnswer(invocation -> {
                    Comment comment = invocation.getArgument(0);
                    comment.setId(32L);
                    return comment;
                });

        CommentRequestDTO request =
                new CommentRequestDTO("Admin comment");

        // Act
        CommentResponseDTO response =
                commentService.addComment(20L, request);

        // Assert
        assertEquals(32L, response.id());
        assertEquals("Admin comment", response.content());
        assertEquals(3L, response.authorId());

        verify(commentRepository).save(any(Comment.class));
    }


}