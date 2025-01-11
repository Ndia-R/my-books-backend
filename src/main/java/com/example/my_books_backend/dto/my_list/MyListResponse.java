package com.example.my_books_backend.dto.my_list;

import java.time.LocalDateTime;
import com.example.my_books_backend.dto.book.BookResponse;
import com.example.my_books_backend.dto.user.SimpleUserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyListResponse {
    private Long id;
    private LocalDateTime updatedAt;
    private SimpleUserInfo user;
    private BookResponse book;
}
