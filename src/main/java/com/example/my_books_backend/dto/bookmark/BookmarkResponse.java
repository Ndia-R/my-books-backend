package com.example.my_books_backend.dto.bookmark;

import java.time.LocalDateTime;
import com.example.my_books_backend.dto.book.BookResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkResponse {
    private Long id;
    private Long userId;
    private String bookId;
    private Integer chapterNumber;
    private Integer pageNumber;
    private String note;
    private String chapterTitle;
    private LocalDateTime updatedAt;
    private BookResponse book;
}
