package com.elshamy.workflow.controller;

import com.elshamy.workflow.dto.UserRequestDTO;
import com.elshamy.workflow.dto.UserResponseDTO;
import com.elshamy.workflow.dto.UserUpdateDTO;
import com.elshamy.workflow.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(
            @Valid @RequestBody UserRequestDTO request) {
        UserResponseDTO created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getUser(){
        return ResponseEntity.ok(userService.getUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserWithId(@PathVariable("id") Long userId){
        return ResponseEntity.ok(userService.getUserWithId(userId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<List<UserResponseDTO>> getUsers(){
        return ResponseEntity.ok(userService.getUsers());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO request) {

        return ResponseEntity.ok(userService.updateUser(id, request));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id){
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}