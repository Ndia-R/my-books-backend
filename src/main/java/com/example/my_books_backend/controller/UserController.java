package com.example.my_books_backend.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.my_books_backend.dto.user.CheckUsernameExistsResponse;
import com.example.my_books_backend.dto.user.ProfileCountsResponse;
import com.example.my_books_backend.dto.user.ChangeEmailRequest;
import com.example.my_books_backend.dto.user.ChangePasswordRequest;
import com.example.my_books_backend.dto.user.UserResponse;
import com.example.my_books_backend.dto.user.UpdateUserRequest;
import com.example.my_books_backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> userResponses = userService.getAllUsers();
        return ResponseEntity.ok(userResponses);
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(userResponse);
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser() {
        UserResponse userResponse = userService.getCurrentUser();
        return ResponseEntity.ok(userResponse);
    }

    @GetMapping("/me/profile-counts")
    public ResponseEntity<ProfileCountsResponse> getProfileCounts() {
        ProfileCountsResponse profileCountsResponse = userService.getProfileCounts();
        return ResponseEntity.ok(profileCountsResponse);
    }

    @PutMapping("/me")
    public ResponseEntity<Void> updateCurrentUser(@Valid @RequestBody UpdateUserRequest request) {
        userService.updateCurrentUser(request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/email")
    public ResponseEntity<Void> changeEmail(@Valid @RequestBody ChangeEmailRequest request) {
        userService.changeEmail(request);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/exists")
    public ResponseEntity<CheckUsernameExistsResponse> checkUsernameExists(
            @RequestParam String name) {
        Boolean exists = userService.checkUsernameExists(name);
        return ResponseEntity.ok(new CheckUsernameExistsResponse(exists));
    }
}
