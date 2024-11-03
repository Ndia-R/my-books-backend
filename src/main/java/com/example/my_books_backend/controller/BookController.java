package com.example.my_books_backend.controller;

import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.my_books_backend.dto.book.BookDto;
import com.example.my_books_backend.dto.book.BookResponseDto;
import com.example.my_books_backend.service.BookService;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("")
    public ResponseEntity<List<BookDto>> getBooks() {
        List<BookDto> books = bookService.getBooks();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable String id) {
        BookDto book = bookService.getBookById(id);
        return ResponseEntity.ok(book);
    }

    @GetMapping("/search")
    public ResponseEntity<BookResponseDto> searchByTitle(@RequestParam String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer maxResults) {
        BookResponseDto bookResponse = bookService.searchByTitle(q, page, maxResults);
        return ResponseEntity.ok(bookResponse);
    }

    @GetMapping("/discover")
    public ResponseEntity<BookResponseDto> searchByGenreId(@RequestParam String genreId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer maxResults) {
        List<String> genreIds = Arrays.asList(genreId.split(","));
        BookResponseDto bookResponse = bookService.searchByGenreId(genreIds, page, maxResults);
        return ResponseEntity.ok(bookResponse);
    }

    @GetMapping("/new-releases")
    public ResponseEntity<List<BookDto>> getNewReleases() {
        List<BookDto> books = bookService.getNewReleases();
        return ResponseEntity.ok(books);
    }

}
