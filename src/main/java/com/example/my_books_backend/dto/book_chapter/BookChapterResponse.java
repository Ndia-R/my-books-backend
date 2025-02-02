package com.example.my_books_backend.dto.book_chapter;

import com.example.my_books_backend.entity.BookChapterId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookChapterResponse {
    private BookChapterId bookChapterId;
    private String title;
}
