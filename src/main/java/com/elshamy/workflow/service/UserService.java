package com.elshamy.workflow.service;

import com.elshamy.workflow.dto.UserRequestDTO;
import com.elshamy.workflow.dto.UserResponseDTO;
import com.elshamy.workflow.dto.UserUpdateDTO;
import com.elshamy.workflow.entity.User;
import com.elshamy.workflow.enums.Role;
import com.elshamy.workflow.exception.AccessDeniedException;
import com.elshamy.workflow.exception.ConflictException;
import com.elshamy.workflow.exception.ResourceNotFoundException;
import com.elshamy.workflow.repository.UserRepository;
import com.elshamy.workflow.security.CurrentUserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, CurrentUserService currentUserService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.currentUserService = currentUserService;
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

    private boolean isAdmin(){
        return currentUserService.getCurrentUser().getRole() == Role.ADMIN;
    }

    public UserResponseDTO getUser(){
        return toDTO(currentUserService.getCurrentUser());
    }

    public UserResponseDTO getUserWithId(Long userId){
        if(!userId.equals(currentUserService.getCurrentUser().getId()) && !isAdmin()){
            throw new AccessDeniedException("Can not be access to the user id: " + userId);
        }
        if(userId.equals(currentUserService.getCurrentUser().getId())){
            return toDTO(currentUserService.getCurrentUser());
        }
        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found with id: " + userId)
        );
        return toDTO(user);
    }

    public List<UserResponseDTO> getUsers(){
        return userRepository.findAll().stream()
                .map(this::toDTO).toList();
    }

    public UserResponseDTO updateUser(Long userId, UserUpdateDTO userUpdateDTO){
        if(!isAdmin() && userUpdateDTO.role() != null){
            throw new AccessDeniedException("Can not edit role");
        }
        if (!isAdmin() && !userId.equals(currentUserService.getCurrentUser().getId())){
            throw new AccessDeniedException("Can not be edit another user");
        }

        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found with id: " + userId)
        );

        if(userUpdateDTO.role() != null){
            user.setRole(userUpdateDTO.role());
        }

        if(userUpdateDTO.email() != null && !userUpdateDTO.email().equals(user.getEmail())){
            if(userRepository.existsByEmail(userUpdateDTO.email())) {
                throw new ConflictException("Email already exists");
            }
            user.setEmail(userUpdateDTO.email());
        }

        if(userUpdateDTO.username() != null && !userUpdateDTO.username().equals(user.getUsername())){
            if(userRepository.existsByUsername(userUpdateDTO.username())) {
                throw new ConflictException("Username already exists");
            }
            user.setUsername(userUpdateDTO.username());
        }

        if(userUpdateDTO.password() != null && !userUpdateDTO.password().isBlank()){
            String password = passwordEncoder.encode(userUpdateDTO.password());
            user.setPassword(password);
        }

        User userSaved = userRepository.save(user);
        return toDTO(userSaved);
    }

    @Transactional
    public void deleteUser(Long userId){
        if(!isAdmin()){
            throw new AccessDeniedException("Only admins can delete users");
        }

        User user = userRepository.findById(userId).orElseThrow(
                () -> new ResourceNotFoundException("User not found with id: " + userId)
        );

        if(!user.getOwnedProjects().isEmpty() || !user.getMemberProjects().isEmpty() || !user.getAssignedTasks().isEmpty()){
            throw new ConflictException("Cannot delete user because they have associated projects or assigned tasks");
        }

        userRepository.delete(user);
    }
}