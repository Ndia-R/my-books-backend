package com.example.my_books_backend.service;

import com.example.my_books_backend.dto.book.BookDetailsResponse;
import com.example.my_books_backend.dto.book.BookPageResponse;

public interface BookService {
    BookDetailsResponse getBookDetailsById(String id);

    BookPageResponse getNewBooks(Integer page, Integer maxResults);

    BookPageResponse getBookPageByTitle(String query, Integer page, Integer maxResults);

    BookPageResponse getBookPageByGenreId(String genreIdsQuery, String conditionQuery, Integer page,
            Integer maxResults);
}
