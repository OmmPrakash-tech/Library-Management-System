package com.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.exception.UserException;
import com.example.payload.dto.SignupDTO;
import com.example.payload.dto.UserDTO;
import com.example.payload.request.ForgotPasswordRequest;
import com.example.payload.request.LoginRequest;
import com.example.payload.request.ResetPasswordRequest;
import com.example.payload.response.ApiResponse;
import com.example.payload.response.AuthResponse;
import com.example.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins="http://localhost:4200")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // SIGNUP
  @PostMapping("/signup")
public ResponseEntity<AuthResponse> signupHandler(
        @RequestBody @Valid SignupDTO req
) throws UserException {

    AuthResponse res = authService.signup(req);

    return ResponseEntity.ok(res);
}

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginHandler(
            @RequestBody @Valid LoginRequest req
    ) {

        AuthResponse res =
                authService.login(req.getEmail(), req.getPassword());

        return ResponseEntity.ok(res);
    }

    // FORGOT PASSWORD
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPasswordHandler(
            @RequestBody @Valid ForgotPasswordRequest request
    ) throws UserException {

        authService.createPasswordResetToken(request.getEmail());

        ApiResponse res = new ApiResponse(
                "A reset link was sent to your email.",
                true
        );

        return ResponseEntity.ok(res);
    }

    // RESET PASSWORD
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(
            @RequestBody @Valid ResetPasswordRequest request) {

        authService.resetPassword(
                request.getToken(),
                request.getPassword()
        );

        ApiResponse res = new ApiResponse(
                "Password reset successful",
                true
        );

        return ResponseEntity.ok(res);
    }
}