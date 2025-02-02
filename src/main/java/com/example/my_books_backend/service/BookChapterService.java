package com.example.my_books_backend.service;

import java.util.List;
import com.example.my_books_backend.dto.book_chapter.BookChapterResponse;

public interface BookChapterService {
    List<BookChapterResponse> getBookChapterByBookId(String bookId);
}
