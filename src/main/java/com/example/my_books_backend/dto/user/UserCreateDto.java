package com.example.my_books_backend.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {

    @NotNull
    @Email
    private String email;

    private String name;

    @NotNull
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    private String avatarUrl;
}
