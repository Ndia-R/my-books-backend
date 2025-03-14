package com.example.my_books_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.my_books_backend.dto.book_chapter.BookTableOfContentsResponse;
import com.example.my_books_backend.service.BookChapterService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class BookChapterController {
    private final BookChapterService bookChapterService;

    @GetMapping("/books/{bookId}/table-of-contents")
    public ResponseEntity<BookTableOfContentsResponse> getBookTableOfContents(
            @PathVariable String bookId) {
        BookTableOfContentsResponse bookTableOfContentsResponse =
                bookChapterService.getBookTableOfContents(bookId);
        return ResponseEntity.ok(bookTableOfContentsResponse);
    }
}
