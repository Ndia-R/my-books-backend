package com.example.my_books_backend.dto.book_chapter_page_content;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookChapterPageContentResponse {
    private String bookId;
    private Integer chapterNumber;
    private String chapterTitle;
    private Integer pageNumber;
    private String content;
}
