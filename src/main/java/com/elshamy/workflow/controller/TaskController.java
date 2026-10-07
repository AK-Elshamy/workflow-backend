package com.elshamy.workflow.controller;

import com.elshamy.workflow.dto.AssignTaskRequestDTO;
import com.elshamy.workflow.dto.TaskRequestDTO;
import com.elshamy.workflow.dto.TaskResponseDTO;
import com.elshamy.workflow.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/tasks")
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO taskRequestDTO){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(taskService.createTask(taskRequestDTO));
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskResponseDTO> getTask(@PathVariable("id") Long id){
        return ResponseEntity.ok(taskService.getTask(id));
    }

    @GetMapping("/projects/{id}/tasks")
    public ResponseEntity<List<TaskResponseDTO>> getProjectTasks(@PathVariable("id") Long projectId){
        return ResponseEntity.ok(taskService.getProjectTasks(projectId));
    }

    @GetMapping("/users/{id}/tasks")
    public ResponseEntity<List<TaskResponseDTO>> getUserTasks(@PathVariable("id") Long userId){
        return ResponseEntity.ok(taskService.getUserTasks(userId));
    }


    @PatchMapping("/tasks/{id}/assignee")
    public ResponseEntity<TaskResponseDTO> assignTask(
            @PathVariable("id") Long taskId,
            @Valid @RequestBody AssignTaskRequestDTO request) {

        return ResponseEntity.ok(
                taskService.assignTask(taskId, request.userId())
        );
    }
}