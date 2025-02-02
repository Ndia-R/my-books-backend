package com.example.my_books_backend.service;

import java.util.List;
import com.example.my_books_backend.dto.book_page.BookPageResponse;

public interface BookPageService {
    List<BookPageResponse> getBookPageByBookId(String bookId);
}
