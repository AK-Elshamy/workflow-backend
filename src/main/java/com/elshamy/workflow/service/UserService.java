package com.elshamy.workflow.service;

import com.elshamy.workflow.dto.UserRequestDTO;
import com.elshamy.workflow.dto.UserResponseDTO;
import com.elshamy.workflow.entity.User;
import com.elshamy.workflow.enums.Role;
import com.elshamy.workflow.exception.ConflictException;
import com.elshamy.workflow.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO){
        if (userRepository.existsByUsername(userRequestDTO.username())) {
            throw new ConflictException("Username already exists");
        }

        if (userRepository.existsByEmail(userRequestDTO.email())) {
            throw new ConflictException("Email already exists");
        }

        String encodedPassword =
                passwordEncoder.encode(userRequestDTO.password());

        User user = new User(
                userRequestDTO.username(),
                userRequestDTO.email(),
                encodedPassword,
                Role.USER
        );

        User savedUser = userRepository.save(user);
        return toDTO(savedUser);
    }
    private UserResponseDTO toDTO(User user){
        return new UserResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }



}