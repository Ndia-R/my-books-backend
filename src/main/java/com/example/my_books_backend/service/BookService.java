package com.example.my_books_backend.service;

import java.util.List;
import com.example.my_books_backend.dto.book.BookResponse;
import com.example.my_books_backend.dto.book.PaginatedBookResponse;

public interface BookService {
    List<BookResponse> getAllBooks();

    BookResponse getBookById(String id);

    PaginatedBookResponse searchByTitle(String q, Integer page, Integer maxResults);

    PaginatedBookResponse searchByGenreId(String genreId, Integer page, Integer maxResults);

    List<BookResponse> getNewReleases();
}
