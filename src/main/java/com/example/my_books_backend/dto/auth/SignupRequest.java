package com.example.my_books_backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    @NotNull
    @NotBlank(message = "メールアドレスは必須です")
    @Email(message = "有効なメールアドレスを入力してください")
    private String email;

    @NotNull
    @NotBlank(message = "パスワードは必須です")
    @Size(min = 3, message = "パスワードは3文字以上で入力してください")
    private String password;

    @NotNull
    @NotBlank(message = "ユーザー名は必須です")
    private String name;

    @NotNull
    @NotBlank(message = "アバターは必須です")
    private String avatarPath;
}
