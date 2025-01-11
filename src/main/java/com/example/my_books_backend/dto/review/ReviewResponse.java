package com.example.my_books_backend.dto.review;

import java.time.LocalDateTime;
import com.example.my_books_backend.dto.book.BookResponse;
import com.example.my_books_backend.dto.user.SimpleUserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private String comment;
    private Double rating;
    private LocalDateTime updatedAt;
    private SimpleUserInfo user;
    private BookResponse book;
}
