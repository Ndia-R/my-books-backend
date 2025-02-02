package com.example.my_books_backend.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.my_books_backend.dto.book_page.BookPageResponse;
import com.example.my_books_backend.service.BookPageService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/book-pages")
@RequiredArgsConstructor
public class BookPageController {
    private final BookPageService bookPageService;

    @GetMapping("/{bookId}")
    public ResponseEntity<List<BookPageResponse>> getBookPagesById(@PathVariable String bookId) {
        List<BookPageResponse> bookPages = bookPageService.getBookPageByBookId(bookId);
        return ResponseEntity.ok(bookPages);
    }
}
