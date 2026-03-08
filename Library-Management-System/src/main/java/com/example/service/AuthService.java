package com.example.service;

import com.example.payload.dto.UserDTO;
import com.example.payload.response.AuthResponse;

public interface AuthService {

    AuthResponse login(String email, String password);

    AuthResponse signup(UserDTO userDTO);

    void createPasswordResetToken(String email);

    void resetPassword(String token, String newPassword);
}