package com.example.my_books_backend.service;

import java.util.List;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.example.my_books_backend.dto.auth.LoginDto;
import com.example.my_books_backend.dto.auth.LoginResponse;
import com.example.my_books_backend.dto.auth.SignupDto;
import com.example.my_books_backend.dto.user.UserCreateDto;
import com.example.my_books_backend.dto.user.UserDto;
import com.example.my_books_backend.exception.ConflictException;
import com.example.my_books_backend.exception.UnAuthorizedException;
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

    public LoginResponse login(LoginDto loginDto) {
        Authentication authentication;
        try {
            authentication =
                    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                            loginDto.getEmail(), loginDto.getPassword()));
        } catch (AuthenticationException e) {
            throw new UnAuthorizedException("Login failed: Invalid email or password.");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();

        String accessToken = jwtUtil.generateToken(user);
        String name = user.getName();
        List<String> roles = user.getRoles().stream().map(role -> role.getName()).toList();

        LoginResponse response = new LoginResponse(accessToken, name, roles);
        return response;
    }

    public UserDto signup(SignupDto signupDto) {
        if (userRepository.existsByEmail(signupDto.getEmail())) {
            throw new ConflictException("User already exists: " + signupDto.getEmail());
        }

        UserCreateDto dto = new UserCreateDto();
        dto.setEmail(signupDto.getEmail());
        dto.setPassword(signupDto.getPassword());

        UserDto userDto = userService.createUser(dto);
        return userDto;
    }
}
