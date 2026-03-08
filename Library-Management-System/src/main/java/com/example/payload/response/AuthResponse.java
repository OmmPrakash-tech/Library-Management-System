package com.example.payload.response;

import com.example.payload.dto.UserDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    private String jwt;
    private String tokenType = "Bearer";
    private String message;
    private String title;
    private UserDTO user;
}