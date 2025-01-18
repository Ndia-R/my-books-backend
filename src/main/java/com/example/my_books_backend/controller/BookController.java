package com.example.my_books_backend.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.my_books_backend.dto.book.BookDetailResponse;
import com.example.my_books_backend.dto.book.BookResponse;
import com.example.my_books_backend.dto.book.PaginatedBookResponse;
import com.example.my_books_backend.service.BookService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping("/search")
    public ResponseEntity<PaginatedBookResponse> searchByTitle(@RequestParam String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer maxResults) {
        PaginatedBookResponse paginatedBookResponse =
                bookService.searchByTitle(q, page, maxResults);
        return ResponseEntity.ok(paginatedBookResponse);
    }

    @GetMapping("/discover")
    public ResponseEntity<PaginatedBookResponse> searchByGenreId(@RequestParam String genreId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer maxResults) {
        PaginatedBookResponse paginatedBookResponse =
                bookService.searchByGenreId(genreId, page, maxResults);
        return ResponseEntity.ok(paginatedBookResponse);
    }

    @GetMapping("/new-releases")
    public ResponseEntity<List<BookResponse>> getNewReleases() {
        List<BookResponse> books = bookService.getNewReleases();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{bookId}")
    public ResponseEntity<BookDetailResponse> getBookById(@PathVariable String bookId) {
        BookDetailResponse book = bookService.getBookDetailById(bookId);
        return ResponseEntity.ok(book);
    }
}
