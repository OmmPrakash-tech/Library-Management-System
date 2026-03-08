package com.example.mapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.model.User;
import com.example.payload.dto.UserDTO;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {

        if (user == null) {
            return null;
        }

        UserDTO userDTO = new UserDTO();

        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setFullName(user.getFullName());
        userDTO.setPhone(user.getPhone());
        userDTO.setLastLogin(user.getLastLogin());
        userDTO.setRole(user.getRole());

        return userDTO;
    }

    public List<UserDTO> toDTOList(List<User> users) {

        if (users == null) {
            return Collections.emptyList();
        }

        return users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Set<UserDTO> toDTOSet(Set<User> users) {

        if (users == null) {
            return Collections.emptySet();
        }

        return users.stream()
                .map(this::toDTO)
                .collect(Collectors.toSet());
    }

    public User toEntity(UserDTO userDTO) {

        if (userDTO == null) {
            return null;
        }

        User user = new User();

        user.setId(userDTO.getId());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        user.setCreatedAt(LocalDateTime.now());
        user.setPhone(userDTO.getPhone());
        user.setFullName(userDTO.getFullName());
        user.setRole(userDTO.getRole());

        return user;
    }
}