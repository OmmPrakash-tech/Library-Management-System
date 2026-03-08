package com.example.payload.dto;

import java.time.LocalDateTime;

import com.example.domain.UserRole;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;

    @NotNull(message="email is required")
    private String email;

    @NotNull(message="password is required")
    private String password;
    private String phone;

    @NotNull(message="Name is required")
    private String fullName;
    private UserRole role;
    private String username;

    private LocalDateTime lastLogin;
}