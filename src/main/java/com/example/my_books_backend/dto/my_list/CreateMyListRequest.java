package com.example.my_books_backend.dto.my_list;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMyListRequest {
    @NotNull
    private String bookId;

    @NotNull
    private Long userId;
}
