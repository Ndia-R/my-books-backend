package com.example.my_books_backend.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ChangeEmailRequest {
    @NotBlank(message = "メールアドレスは必須です")
    @Email
    private String email;

    @NotBlank(message = "パスワードは必須です")
    private String password;
}
