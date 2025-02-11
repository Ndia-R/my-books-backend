package com.example.my_books_backend.service;

import java.util.List;
import com.example.my_books_backend.dto.book.BookResponse;
import com.example.my_books_backend.dto.book.BookPageResponse;

public interface BookService {
    BookResponse getBookById(String id);

    List<BookResponse> getNewBooks();

    BookPageResponse searchByTitle(String q, Integer page, Integer maxResults);

    BookPageResponse searchByGenreId(String genreId, Integer page, Integer maxResults);
}
