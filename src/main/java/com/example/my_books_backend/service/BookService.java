package com.example.my_books_backend.service;

import java.util.List;
import com.example.my_books_backend.dto.book.BookDto;
import com.example.my_books_backend.dto.book.BookResponseDto;

public interface BookService {

    List<BookDto> getBooks();

    BookDto getBookById(String id);

    BookResponseDto searchByTitle(String q, Integer page, Integer maxResults);

    BookResponseDto searchByGenreId(String genreId, Integer page, Integer maxResults);

    List<BookDto> getNewReleases();
}
