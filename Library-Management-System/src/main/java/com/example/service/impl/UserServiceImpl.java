package com.example.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.mapper.UserMapper;
import com.example.model.User;
import com.example.payload.dto.UserDTO;
import com.example.repository.UserRepository;
import com.example.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
     private final UserMapper userMapper;   // ✅ must be final


     @Override
   public User getCurrentUser(){
 String email = SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getName();

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("user not found!"));

    return user;
   }


   @Override
public List<UserDTO> getAllUsers() {

    List<User> users = userRepository.findAll();

    return users.stream()
            .map(userMapper::toDTO)
            .collect(Collectors.toList());
}


  @Override
public User findById(Long id) {
    return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with given id"));
}

@Override
public void deleteUser(Long id) {

    User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with given id"));

    userRepository.delete(user);
}

@Override
public UserDTO updateUser(Long id, UserDTO userDTO) {

    // 1. Fetch existing user
    User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found with given id"));

    // 2. Update fields (only allowed ones)
    user.setFullName(userDTO.getFullName());
    user.setPhone(userDTO.getPhone());

    // Optional updates (only if provided)
    if (userDTO.getEmail() != null) {
        user.setEmail(userDTO.getEmail());
    }

    if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
        user.setPassword(userDTO.getPassword()); // (later you can encode it 🔐)
    }

    if (userDTO.getRole() != null) {
        user.setRole(userDTO.getRole());
    }

    // 3. Save updated user
    User updatedUser = userRepository.save(user);

    // 4. Convert to DTO and return
    return userMapper.toDTO(updatedUser);
}


}