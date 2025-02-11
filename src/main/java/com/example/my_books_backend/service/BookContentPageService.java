package com.example.my_books_backend.service;

import com.example.my_books_backend.dto.book_content_page.BookContentPageResponse;

public interface BookContentPageService {
    BookContentPageResponse getBookContentPage(String bookId, Integer chapterNumber,
            Integer pageNumber);
}
