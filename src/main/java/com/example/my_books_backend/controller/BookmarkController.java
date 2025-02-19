package com.example.my_books_backend.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.example.my_books_backend.dto.bookmark.BookmarkPageResponse;
import com.example.my_books_backend.dto.bookmark.BookmarkRequest;
import com.example.my_books_backend.dto.bookmark.BookmarkResponse;
import com.example.my_books_backend.entity.User;
import com.example.my_books_backend.service.BookmarkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @GetMapping("/bookmarks/{bookId}")
    public ResponseEntity<List<BookmarkResponse>> getBookmarksByBookId(@PathVariable String bookId,
            @AuthenticationPrincipal User user) {
        List<BookmarkResponse> bookmarkResponses =
                bookmarkService.getBookmarksByBookId(bookId, user);
        return ResponseEntity.ok(bookmarkResponses);
    }

    @GetMapping("/bookmarks")
    public ResponseEntity<BookmarkPageResponse> getBookmarkPageByUser(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer maxResults,
            @AuthenticationPrincipal User user) {
        BookmarkPageResponse bookmarkPageResponses =
                bookmarkService.getBookmarkPageByUser(page, maxResults, user);
        return ResponseEntity.ok(bookmarkPageResponses);
    }

    @PostMapping("/bookmarks")
    public ResponseEntity<BookmarkResponse> createBookmark(
            @Valid @RequestBody BookmarkRequest request, @AuthenticationPrincipal User user) {
        BookmarkResponse bookmarkResponse = bookmarkService.createBookmark(request, user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(bookmarkResponse.getId()).toUri();
        return ResponseEntity.created(location).body(bookmarkResponse);
    }

    @PutMapping("/bookmarks/{id}")
    public ResponseEntity<BookmarkResponse> updateBookmark(@PathVariable Long id,
            @Valid @RequestBody BookmarkRequest request, @AuthenticationPrincipal User user) {
        BookmarkResponse bookmarkResponse = bookmarkService.updateBookmark(id, request, user);
        return ResponseEntity.ok(bookmarkResponse);
    }

    @DeleteMapping("/bookmarks/{id}")
    public ResponseEntity<Void> deleteBookmark(@PathVariable Long id,
            @AuthenticationPrincipal User user) {
        bookmarkService.deleteBookmark(id, user);
        return ResponseEntity.noContent().build();
    }
}
