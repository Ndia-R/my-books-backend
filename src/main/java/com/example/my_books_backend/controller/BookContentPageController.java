package com.example.my_books_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.my_books_backend.dto.book_content_page.BookContentPageResponse;
import com.example.my_books_backend.service.BookContentPageService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BookContentPageController {
    private final BookContentPageService bookContentPageService;

    @GetMapping("/read/books/{bookId}/chapters/{chapterNumber}/pages/{pageNumber}")
    public ResponseEntity<BookContentPageResponse> getBookContentPage(@PathVariable String bookId,
            @PathVariable Integer chapterNumber, @PathVariable Integer pageNumber) {
        BookContentPageResponse bookContentPageResponse =
                bookContentPageService.getBookContentPage(bookId, chapterNumber, pageNumber);
        return ResponseEntity.ok(bookContentPageResponse);
    }
}
