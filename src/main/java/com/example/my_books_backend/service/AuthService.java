package com.example.my_books_backend.service;

import java.util.List;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import com.example.my_books_backend.dto.auth.LoginRequest;
import com.example.my_books_backend.dto.auth.LoginResponse;
import com.example.my_books_backend.dto.auth.SignupRequest;
import com.example.my_books_backend.dto.auth.AccessTokenResponse;
import com.example.my_books_backend.dto.user.CreateUserRequest;
import com.example.my_books_backend.dto.user.UserResponse;
import com.example.my_books_backend.entity.User;
import com.example.my_books_backend.exception.BadRequestException;
import com.example.my_books_backend.exception.ConflictException;
import com.example.my_books_backend.exception.UnauthorizedException;
import com.example.my_books_backend.repository.UserRepository;
import com.example.my_books_backend.service.impl.UserDetailsServiceImpl;
import com.example.my_books_backend.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserService userService;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request, HttpServletResponse response) {
        Authentication authentication;
        try {
            authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword()));
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("ログインに失敗しました。メールアドレスまたはパスワードが無効です。");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        Cookie refreshTokenCookie = jwtUtil.createRefreshTokenCookie(refreshToken);
        response.addCookie(refreshTokenCookie);

        String name = user.getName();
        List<String> roles =
                user.getRoles().stream().map(role -> role.getName().toString()).toList();

        return new LoginResponse(accessToken, name, roles);
    }

    public UserResponse signup(SignupRequest request) {
        String name = request.getName();
        String email = request.getEmail();
        String password = request.getPassword();

        if (userRepository.existsByName(name)) {
            throw new ConflictException("サインアップに失敗しました。このユーザー名は既に登録されています。: " + name);
        }

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("サインアップに失敗しました。このメールアドレスは既に登録されています。: " + email);
        }

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setName(name);
        createUserRequest.setEmail(email);
        createUserRequest.setPassword(password);

        return userService.createUser(createUserRequest);
    }

    public AccessTokenResponse refreshAccessToken(HttpServletRequest request) {
        String refreshToken = jwtUtil.getRefreshTokenFromCookie(request);

        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)
                || jwtUtil.isTokenInvalid(refreshToken)) {
            throw new UnauthorizedException("トークンが無効です。");
        }

        String email = jwtUtil.getSubjectFromToken(refreshToken);
        User user = (User) userDetailsService.loadUserByUsername(email);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtil.generateAccessToken(user);
        return new AccessTokenResponse(accessToken);
    }

    public void validateToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
            throw new UnauthorizedException("トークンが無効です。");
        }
        String accessToken = bearerToken.substring(7);
        if (!jwtUtil.validateToken(accessToken)) {
            throw new UnauthorizedException("トークンが無効です。");
        }
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtUtil.getRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            throw new BadRequestException("ログアウトに失敗しました。");
        }

        jwtUtil.addInvalidatedTokens(refreshToken);
        Cookie cookie = jwtUtil.getInvalidateRefreshTokenCookie();
        response.addCookie(cookie);
    }
}
