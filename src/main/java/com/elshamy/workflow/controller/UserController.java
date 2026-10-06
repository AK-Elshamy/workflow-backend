package com.elshamy.workflow.controller;

import com.elshamy.workflow.dto.UserRequestDTO;
import com.elshamy.workflow.dto.UserResponseDTO;
import com.elshamy.workflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO userCreated = userService.createUser(userRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }
}