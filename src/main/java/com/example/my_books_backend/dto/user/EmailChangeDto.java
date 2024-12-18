package com.example.my_books_backend.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EmailChangeDto {

    @NotBlank(message = "新しいメールアドレスは必須です")
    @Email
    private String newEmail;
}
