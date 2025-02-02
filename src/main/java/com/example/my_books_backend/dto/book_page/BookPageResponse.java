package com.example.my_books_backend.dto.book_page;

import com.example.my_books_backend.entity.BookPageId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookPageResponse {
    private BookPageId bookPageId;
    private String content;
}
