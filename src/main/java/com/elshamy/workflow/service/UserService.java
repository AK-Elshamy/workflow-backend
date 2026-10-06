package com.elshamy.workflow.service;

import com.elshamy.workflow.dto.UserRequestDTO;
import com.elshamy.workflow.dto.UserResponseDTO;
import com.elshamy.workflow.entity.User;
import com.elshamy.workflow.enums.Role;
import com.elshamy.workflow.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO){
        User user = new User(userRequestDTO.username(), userRequestDTO.email(), userRequestDTO.password(), Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(user);
        return toDTO(savedUser);
    }
    private UserResponseDTO toDTO(User user){
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().toString(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }



}