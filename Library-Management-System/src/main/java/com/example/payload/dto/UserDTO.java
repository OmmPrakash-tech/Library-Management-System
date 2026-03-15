package com.example.payload.dto;

import java.time.LocalDateTime;

import com.example.domain.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;

    @NotBlank(message="Email is required")
    @Email(message="Invalid email format")
    private String email;

    @NotBlank(message="Password is required")
    @Size(min=6, message="Password must be at least 6 characters")
    private String password;

    private String phone;

    @NotBlank(message="Name is required")
    private String fullName;

    private UserRole role;

    private LocalDateTime lastLogin;
}