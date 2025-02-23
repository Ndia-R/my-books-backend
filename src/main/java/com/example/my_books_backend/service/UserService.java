package com.example.my_books_backend.service;

import java.util.List;
import java.util.Optional;
import com.example.my_books_backend.dto.user.ChangeEmailRequest;
import com.example.my_books_backend.dto.user.ChangePasswordRequest;
import com.example.my_books_backend.dto.user.CreateUserRequest;
import com.example.my_books_backend.dto.user.ProfileCountsResponse;
import com.example.my_books_backend.dto.user.UserResponse;
import com.example.my_books_backend.dto.user.UpdateUserRequest;
import com.example.my_books_backend.entity.User;

public interface UserService {
    Optional<User> findByEmail(String email);

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Long id);

    User createUser(CreateUserRequest request);

    void deleteUser(Long id);

    UserResponse getCurrentUser(User user);

    ProfileCountsResponse getProfileCounts(User user);

    void updateCurrentUser(UpdateUserRequest request, User user);

    void changeEmail(ChangeEmailRequest request, User user);

    void changePassword(ChangePasswordRequest request, User user);
}
