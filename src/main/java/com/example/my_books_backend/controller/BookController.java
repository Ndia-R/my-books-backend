package com.example.my_books_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.my_books_backend.dto.book.BookDetailsResponse;
import com.example.my_books_backend.dto.book.BookPageResponse;
import com.example.my_books_backend.service.BookService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping("/books/{bookId}")
    public ResponseEntity<BookDetailsResponse> getBookDetailsById(@PathVariable String bookId) {
        BookDetailsResponse bookDetailsResponse = bookService.getBookDetailsById(bookId);
        return ResponseEntity.ok(bookDetailsResponse);
    }

    @GetMapping("/books/new-books")
    public ResponseEntity<BookPageResponse> getNewBooks(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer maxResults) {
        BookPageResponse bookPageResponse = bookService.getNewBooks(page, maxResults);
        return ResponseEntity.ok(bookPageResponse);
    }

    @GetMapping("/books/search")
    public ResponseEntity<BookPageResponse> getBookPageByTitle(@RequestParam String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer maxResults) {
        BookPageResponse bookPageResponse = bookService.getBookPageByTitle(q, page, maxResults);
        return ResponseEntity.ok(bookPageResponse);
    }

    @GetMapping("/books/discover")
    public ResponseEntity<BookPageResponse> getBookPageByGenreId(@RequestParam String genreIds,
            @RequestParam String condition, @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer maxResults) {
        BookPageResponse bookPageResponse =
                bookService.getBookPageByGenreId(genreIds, condition, page, maxResults);
        return ResponseEntity.ok(bookPageResponse);
    }
}
