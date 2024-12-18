package com.example.my_books_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.my_books_backend.dto.auth.LoginDto;
import com.example.my_books_backend.dto.auth.LoginResponseDto;
import com.example.my_books_backend.dto.auth.SignupDto;
import com.example.my_books_backend.dto.user.UserDto;
import com.example.my_books_backend.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginDto loginDto) {
        LoginResponseDto loginResponseDto = authService.login(loginDto);
        return ResponseEntity.ok(loginResponseDto);
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@Valid @RequestBody SignupDto signupDto) {
        UserDto user = authService.signup(signupDto);
        return ResponseEntity.ok(user);
    }
}
