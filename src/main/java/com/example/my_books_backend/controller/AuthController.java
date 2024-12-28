package com.example.my_books_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.my_books_backend.dto.auth.LoginRequest;
import com.example.my_books_backend.dto.auth.LoginResponse;
import com.example.my_books_backend.dto.auth.SignupRequest;
import com.example.my_books_backend.dto.auth.AccessTokenResponse;
import com.example.my_books_backend.dto.user.UserResponse;
import com.example.my_books_backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(request, response);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignupRequest request) {
        UserResponse userResponse = authService.signup(request);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AccessTokenResponse> refreshToken(HttpServletRequest request) {
        AccessTokenResponse accessTokenResponse = authService.refreshAccessToken(request);
        return ResponseEntity.ok(accessTokenResponse);
    }

    @PostMapping("/validate-token")
    public ResponseEntity<Void> validateToken(HttpServletRequest request) {
        authService.validateToken(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.ok().build();
    }
}
