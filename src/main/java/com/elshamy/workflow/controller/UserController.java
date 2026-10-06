package com.elshamy.workflow.controller;

import com.elshamy.workflow.dto.UserRequestDTO;
import com.elshamy.workflow.dto.UserResponseDTO;
import com.elshamy.workflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("users")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO){
       UserResponseDTO userCreated =  userService.createUser(userRequestDTO);
       return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }
}