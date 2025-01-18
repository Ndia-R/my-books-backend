package com.example.my_books_backend.service;

import java.util.List;
import com.example.my_books_backend.dto.book.BookDetailResponse;
import com.example.my_books_backend.dto.book.BookResponse;
import com.example.my_books_backend.dto.book.PaginatedBookResponse;

public interface BookService {
    PaginatedBookResponse searchByTitle(String q, Integer page, Integer maxResults);

    PaginatedBookResponse searchByGenreId(String genreId, Integer page, Integer maxResults);

    List<BookResponse> getNewReleases();

    BookDetailResponse getBookDetailById(String bookId);
}
