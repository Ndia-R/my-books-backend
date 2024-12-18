package com.example.my_books_backend.service;

import java.util.List;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.example.my_books_backend.dto.auth.LoginDto;
import com.example.my_books_backend.dto.auth.LoginResponseDto;
import com.example.my_books_backend.dto.auth.SignupDto;
import com.example.my_books_backend.dto.user.UserCreateDto;
import com.example.my_books_backend.dto.user.UserDto;
import com.example.my_books_backend.exception.ConflictException;
import com.example.my_books_backend.exception.UnauthorizedException;
import com.example.my_books_backend.exception.ValidationException;
import com.example.my_books_backend.model.User;
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

    public LoginResponseDto login(LoginDto loginDto) {
        Authentication authentication;
        try {
            authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                            loginDto.getEmail(), loginDto.getPassword()));
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("ログインに失敗しました。メールアドレスまたはパスワードが無効です。");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();

        String accessToken = jwtUtil.generateToken(user);
        String name = user.getName();
        List<String> roles = user.getRoles().stream().map(role -> role.getName()).toList();

        return new LoginResponseDto(accessToken, name, roles);
    }

    public UserDto signup(SignupDto signupDto) {
        if (!signupDto.getPassword().equals(signupDto.getConfirmPassword())) {
            throw new ValidationException("パスワードと確認用パスワードが一致していません。");
        }

        if (userRepository.existsByEmail(signupDto.getEmail())) {
            throw new ConflictException(
                    "サインアップに失敗しました。このメールアドレスは既に登録されています。: " + signupDto.getEmail());
        }

        UserCreateDto dto = new UserCreateDto();
        dto.setEmail(signupDto.getEmail());
        dto.setPassword(signupDto.getPassword());

        return userService.createUser(dto);
    }
}
