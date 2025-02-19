package com.example.my_books_backend.controller;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.example.my_books_backend.dto.favorite.FavoriteRequest;
import com.example.my_books_backend.dto.favorite.FavoriteResponse;
import com.example.my_books_backend.entity.User;
import com.example.my_books_backend.dto.favorite.FavoriteInfoResponse;
import com.example.my_books_backend.dto.favorite.FavoritePageResponse;
import com.example.my_books_backend.service.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    @GetMapping("/books/{bookId}/favorites/info")
    public ResponseEntity<FavoriteInfoResponse> getFavoriteInfo(@PathVariable String bookId,
            @RequestParam(required = false) Long userId) {
        FavoriteInfoResponse favoriteInfoResponse = favoriteService.getFavoriteInfo(bookId, userId);
        return ResponseEntity.ok(favoriteInfoResponse);
    }

    @GetMapping("/favorites/{bookId}")
    public ResponseEntity<FavoriteResponse> getFavoriteByBookId(@PathVariable String bookId,
            @AuthenticationPrincipal User user) {
        FavoriteResponse favoriteResponse = favoriteService.getFavoriteByBookId(bookId, user);
        return ResponseEntity.ok(favoriteResponse);
    }

    @GetMapping("/favorites")
    public ResponseEntity<FavoritePageResponse> getFavoritePageByUser(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer maxResults,
            @AuthenticationPrincipal User user) {
        FavoritePageResponse favoritePageResponse =
                favoriteService.getFavoritePageByUser(page, maxResults, user);
        return ResponseEntity.ok(favoritePageResponse);
    }

    @PostMapping("/favorites")
    public ResponseEntity<FavoriteResponse> createFavorite(
            @Valid @RequestBody FavoriteRequest request, @AuthenticationPrincipal User user) {
        FavoriteResponse favoriteResponse = favoriteService.createFavorite(request, user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{bookId}")
                .buildAndExpand(favoriteResponse.getBookId()).toUri();
        return ResponseEntity.created(location).body(favoriteResponse);
    }

    @DeleteMapping("/favorites/{bookId}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable String bookId,
            @AuthenticationPrincipal User user) {
        favoriteService.deleteFavorite(bookId, user);
        return ResponseEntity.noContent().build();
    }
}
