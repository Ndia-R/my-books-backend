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

    void deleteUser(Long id);

    UserResponse createUser(CreateUserRequest request);

    UserResponse getCurrentUser();

    ProfileCountsResponse getProfileCounts();

    void updateCurrentUser(UpdateUserRequest request);

    void changeEmail(ChangeEmailRequest request);

    void changePassword(ChangePasswordRequest request);

    Boolean checkUsernameExists(String name);
}
