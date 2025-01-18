package com.example.my_books_backend.dto.favorite;

import java.time.LocalDateTime;
import com.example.my_books_backend.dto.book.BookResponse;
import com.example.my_books_backend.dto.user.SimpleUserInfo;
import com.example.my_books_backend.entity.FavoriteId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponse {
    private FavoriteId favoriteId;
    private LocalDateTime updatedAt;
    private SimpleUserInfo user;
    private BookResponse book;
}
