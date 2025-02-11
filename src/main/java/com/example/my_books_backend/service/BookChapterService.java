package com.example.my_books_backend.service;

import com.example.my_books_backend.dto.book_chapter.BookTableOfContentsResponse;

public interface BookChapterService {
    BookTableOfContentsResponse getBookTableOfContents(String bookId);
}
