package com.elshamy.workflow.controller;

import com.elshamy.workflow.dto.CommentRequestDTO;
import com.elshamy.workflow.dto.CommentResponseDTO;
import com.elshamy.workflow.dto.CommentUpdateDTO;
import com.elshamy.workflow.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/tasks/{id}/comments")
    public ResponseEntity<CommentResponseDTO> addComment(
            @PathVariable("id") Long taskId,
            @Valid @RequestBody CommentRequestDTO commentRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(taskId, commentRequestDTO));
    }

    @GetMapping("/tasks/{id}/comments")
    public ResponseEntity<List<CommentResponseDTO>> getComments(
            @PathVariable("id") Long taskId
    ) {
        return ResponseEntity.ok(commentService.getComments(taskId));
    }

    @PatchMapping("/comments/{id}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable("id") Long commentId,
            @Valid @RequestBody CommentUpdateDTO commentUpdateDTO
    ) {
        return ResponseEntity.ok(commentService.updateComment(commentId, commentUpdateDTO));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable("id") Long commentId){
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}