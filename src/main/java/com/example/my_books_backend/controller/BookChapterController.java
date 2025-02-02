package com.example.my_books_backend.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.my_books_backend.dto.book_chapter.BookChapterResponse;
import com.example.my_books_backend.service.BookChapterService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/book-chapters")
@RequiredArgsConstructor
public class BookChapterController {
    private final BookChapterService bookChapterService;

    @GetMapping("/{bookId}")
    public ResponseEntity<List<BookChapterResponse>> getBookChaptersById(
            @PathVariable String bookId) {
        List<BookChapterResponse> bookChapters = bookChapterService.getBookChapterByBookId(bookId);
        return ResponseEntity.ok(bookChapters);
    }
}
