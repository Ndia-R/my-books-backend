package com.example.my_books_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.my_books_backend.dto.auth.LoginRequest;
import com.example.my_books_backend.dto.auth.SignupRequest;
import com.example.my_books_backend.dto.auth.AccessTokenResponse;
import com.example.my_books_backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AccessTokenResponse> login(@Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        AccessTokenResponse loginResponse = authService.login(request, response);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<AccessTokenResponse> signup(@Valid @RequestBody SignupRequest request,
            HttpServletResponse response) {
        AccessTokenResponse userResponse = authService.signup(request, response);
        return ResponseEntity.ok(userResponse);
    }

    // Controllerクラスで"/logout"のエンドポイントを用意しても、Spring Securityのデフォルトの
    // "/logout"が呼ばれるので、このエンドポイントは意味がなくなる
    // ログアウト処理は「SecurityConfig.java」に実装している
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        authService.logout(response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AccessTokenResponse> refreshToken(HttpServletRequest request) {
        AccessTokenResponse accessTokenResponse = authService.refreshAccessToken(request);
        return ResponseEntity.ok(accessTokenResponse);
    }
}
