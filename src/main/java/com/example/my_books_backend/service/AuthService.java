package com.example.my_books_backend.service;

import java.util.List;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.example.my_books_backend.dto.auth.LoginRequest;
import com.example.my_books_backend.dto.auth.LoginResponse;
import com.example.my_books_backend.dto.auth.SignupRequest;
import com.example.my_books_backend.dto.auth.TokenRefreshRequest;
import com.example.my_books_backend.dto.auth.TokenRefreshResponse;
import com.example.my_books_backend.dto.user.CreateUserRequest;
import com.example.my_books_backend.dto.user.UserResponse;
import com.example.my_books_backend.entity.User;
import com.example.my_books_backend.exception.ConflictException;
import com.example.my_books_backend.exception.UnauthorizedException;
import com.example.my_books_backend.exception.ValidationException;
import com.example.my_books_backend.repository.UserRepository;
import com.example.my_books_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("ログインに失敗しました。メールアドレスまたはパスワードが無効です。");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        String name = user.getName();
        List<String> roles = user.getRoles().stream().map(role -> role.getName()).toList();

        return new LoginResponse(accessToken, refreshToken, name, roles);
    }

    public UserResponse signup(SignupRequest signupRequest) {
        String email = signupRequest.getEmail();
        String password = signupRequest.getPassword();
        String confirmPassword = signupRequest.getConfirmPassword();

        if (!password.equals(confirmPassword)) {
            throw new ValidationException("パスワードと確認用パスワードが一致していません。");
        }

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("サインアップに失敗しました。このメールアドレスは既に登録されています。: " + email);
        }

        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setEmail(email);
        createUserRequest.setPassword(password);

        return userService.createUser(createUserRequest);
    }

    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        if (jwtUtil.validateRefreshToken(requestRefreshToken)) {
            String email = jwtUtil.getSubjectFromToken(requestRefreshToken);
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UnauthorizedException("ユーザーが見つかりません。"));

            String newAccessToken = jwtUtil.generateAccessToken(user);
            return new TokenRefreshResponse(newAccessToken, requestRefreshToken);
        } else {
            throw new UnauthorizedException("リフレッシュトークンが無効です。");
        }
    }
}
