package com.example.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.config.JwtProvider;
import com.example.domain.UserRole;
import com.example.exception.UserException;
import com.example.mapper.UserMapper;
import com.example.model.PasswordResetToken;
import com.example.model.User;
import com.example.payload.dto.UserDTO;
import com.example.payload.response.AuthResponse;
import com.example.repository.PasswordResetTokenRepository;
import com.example.repository.UserRepository;
import com.example.service.AuthService;
import com.example.service.EmailService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserMapper userMapper;

    // LOGIN
    @Override
    public AuthResponse login(String email, String password) {

        Authentication authentication = authenticate(email, password);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.generateToken(authentication);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("User not found"));

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        AuthResponse response = new AuthResponse();
        response.setTitle("Login success");
        response.setMessage("Welcome Back " + email);
        response.setJwt(token);
        response.setUser(userMapper.toDTO(user));

        return response;
    }

    // SIGNUP
    @Override
    public AuthResponse signup(UserDTO req) {

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new UserException("Email already registered");
        }

        User createdUser = new User();

        createdUser.setEmail(req.getEmail());
        createdUser.setPassword(passwordEncoder.encode(req.getPassword()));
        createdUser.setPhone(req.getPhone());
        createdUser.setFullName(req.getFullName());
        createdUser.setLastLogin(LocalDateTime.now());
        createdUser.setRole(UserRole.ROLE_USER);

        User savedUser = userRepository.save(createdUser);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                savedUser.getEmail(),
                savedUser.getPassword()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = jwtProvider.generateToken(auth);

        AuthResponse response = new AuthResponse();
        response.setJwt(jwt);
        response.setTitle("Welcome " + savedUser.getFullName());
        response.setMessage("Register success");
        response.setUser(userMapper.toDTO(savedUser));

        return response;
    }

    // CREATE PASSWORD RESET TOKEN
    @Override
    @Transactional
    public void createPasswordResetToken(String email) {

        String frontendUrl = "http://localhost:3000/reset-password?token=";

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException("User not found"));

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(5))
                .build();

        passwordResetTokenRepository.save(resetToken);

        String resetLink = frontendUrl + token;

        String subject = "Password Reset Request";

        String body = "Use this link to reset your password (valid for 5 minutes): "
                + resetLink;

        emailService.sendEmail(user.getEmail(), subject, body);
    }

    // RESET PASSWORD
    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {

        Optional<PasswordResetToken> resetTokenOpt =
                passwordResetTokenRepository.findByToken(token);

        PasswordResetToken resetToken = resetTokenOpt
                .orElseThrow(() -> new UserException("Invalid token"));

        if (resetToken.isExpired()) {
            passwordResetTokenRepository.delete(resetToken);
            throw new UserException("Token expired");
        }

        User user = resetToken.getUser();

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);

        passwordResetTokenRepository.delete(resetToken);
    }

    // AUTHENTICATION METHOD
    private Authentication authenticate(String username, String password) {

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password);

        return authenticationManager.authenticate(authToken);
    }
}