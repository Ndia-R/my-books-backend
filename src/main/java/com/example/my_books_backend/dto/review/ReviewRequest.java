package com.example.my_books_backend.dto.review;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    @NotNull
    private String bookId;

    @NotNull
    private String comment;

    @NotNull
    private Double rating;
}
