package com.example.my_books_backend.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ChangePasswordRequest {
    @NotBlank(message = "現在のパスワードは必須です")
    private String currentPassword;

    @NotBlank(message = "新しいパスワードは必須です")
    @Size(min = 4, message = "パスワードは4文字以上で入力してください")
    private String newPassword;

    @NotBlank(message = "確認用パスワードは必須です")
    private String confirmPassword;
}
