package com.example.my_books_backend.controller;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.example.my_books_backend.dto.bookmark.BookmarkRequest;
import com.example.my_books_backend.dto.bookmark.BookmarkResponse;
import com.example.my_books_backend.dto.bookmark.BookmarkPageResponse;
import com.example.my_books_backend.service.BookmarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @GetMapping("/me/bookmarks")
    public ResponseEntity<BookmarkPageResponse> getBookmarks(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer maxResults) {
        BookmarkPageResponse bookmarkPageResponse = bookmarkService.getBookmarks(page, maxResults);
        return ResponseEntity.ok(bookmarkPageResponse);
    }

    @GetMapping("/me/bookmarks/{bookId}")
    public ResponseEntity<BookmarkResponse> getBookmarkByBookId(@PathVariable String bookId) {
        BookmarkResponse bookmarkResponse = bookmarkService.getBookmarkByBookId(bookId);
        return ResponseEntity.ok(bookmarkResponse);
    }

    @PostMapping("/me/bookmarks")
    public ResponseEntity<BookmarkResponse> createBookmark(
            @Valid @RequestBody BookmarkRequest request) {
        BookmarkResponse bookmarkResponse = bookmarkService.createBookmark(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{bookId}")
                .buildAndExpand(bookmarkResponse.getBookId()).toUri();
        return ResponseEntity.created(location).body(bookmarkResponse);
    }

    @PutMapping("/me/bookmarks")
    public ResponseEntity<BookmarkResponse> updateBookmark(
            @Valid @RequestBody BookmarkRequest request) {
        BookmarkResponse bookmarkResponse = bookmarkService.updateBookmark(request);
        return ResponseEntity.ok(bookmarkResponse);
    }

    @DeleteMapping("/me/bookmarks/{bookId}")
    public ResponseEntity<Void> deleteBookmark(@PathVariable String bookId) {
        bookmarkService.deleteBookmark(bookId);
        return ResponseEntity.noContent().build();
    }
}
