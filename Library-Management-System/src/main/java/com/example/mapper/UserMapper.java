package com.example.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.model.User;
import com.example.payload.dto.SignupDTO;
import com.example.payload.dto.UpdateUserDTO;
import com.example.payload.dto.UserDTO;

@Component
public class UserMapper {

    public UserDTO toDTO(User user) {

        if (user == null) {
            return null;
        }

        UserDTO dto = new UserDTO();

        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setLastLogin(user.getLastLogin());
        dto.setRole(user.getRole());

        // 🔥 ADD THIS
        dto.setProfileImage(user.getProfileImage());

        return dto;
    }

    public User toEntity(SignupDTO dto) {

    if (dto == null) return null;

    User user = new User();

    user.setEmail(dto.getEmail());
    user.setPassword(dto.getPassword());
    user.setFullName(dto.getFullName());
    user.setPhone(dto.getPhone());
    user.setProfileImage(dto.getProfileImage());

    return user;
}


public void updateEntity(User user, UpdateUserDTO dto) {

    if (dto == null || user == null) return;

    if (dto.getFullName() != null) {
        user.setFullName(dto.getFullName());
    }

    if (dto.getPhone() != null) {
        user.setPhone(dto.getPhone());
    }

    if (dto.getEmail() != null) {
        user.setEmail(dto.getEmail());
    }

    if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
        user.setPassword(dto.getPassword());
    }

    if (dto.getProfileImage() != null) {
        user.setProfileImage(dto.getProfileImage());
    }
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

    public User toEntity(UserDTO dto) {

        if (dto == null) {
            return null;
        }

        User user = new User();

        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setPhone(dto.getPhone());
        user.setFullName(dto.getFullName());
        user.setRole(dto.getRole());

        // 🔥 ADD THIS
        user.setProfileImage(dto.getProfileImage());

        return user;
    }
}