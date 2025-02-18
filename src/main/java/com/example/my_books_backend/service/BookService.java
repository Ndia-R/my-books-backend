package com.example.my_books_backend.service;

import java.util.List;
import com.example.my_books_backend.dto.book.BookResponse;
import com.example.my_books_backend.dto.book.BookDetailsResponse;
import com.example.my_books_backend.dto.book.BookPageResponse;

public interface BookService {
    BookDetailsResponse getBookDetailsById(String id);

    List<BookResponse> getNewBooks();

    BookPageResponse getBookPageByTitle(String q, Integer page, Integer maxResults);

    BookPageResponse getBookPageByGenreId(String genreId, Integer page, Integer maxResults);
}
