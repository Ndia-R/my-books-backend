package com.example.my_books_backend.service;

import java.util.HashSet;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.my_books_backend.exception.ConflictException;
import com.example.my_books_backend.model.User;
import com.example.my_books_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public User signup(String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("User already exists: " + email);
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(new HashSet<>());

        return userRepository.save(user);
    }
}
