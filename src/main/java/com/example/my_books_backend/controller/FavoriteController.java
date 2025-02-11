package com.example.my_books_backend.controller;

import java.net.URI;
import org.springframework.http.ResponseEntity;
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
import com.example.my_books_backend.dto.favorite.FavoriteCountResponse;
import com.example.my_books_backend.dto.favorite.FavoritePageResponse;
import com.example.my_books_backend.service.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    @GetMapping("/me/favorites")
    public ResponseEntity<FavoritePageResponse> getFavorites(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer maxResults) {
        FavoritePageResponse favoritePage = favoriteService.getFavorites(page, maxResults);
        return ResponseEntity.ok(favoritePage);
    }

    @GetMapping("/me/favorites/{bookId}")
    public ResponseEntity<FavoriteResponse> getFavoriteByBookId(@PathVariable String bookId) {
        FavoriteResponse favoriteResponse = favoriteService.getFavoriteByBookId(bookId);
        return ResponseEntity.ok(favoriteResponse);
    }

    @GetMapping("/books/{bookId}/favorites/count")
    public ResponseEntity<FavoriteCountResponse> getFavoriteCount(@PathVariable String bookId) {
        FavoriteCountResponse favoriteCountResponse = favoriteService.getFavoriteCount(bookId);
        return ResponseEntity.ok(favoriteCountResponse);
    }

    @PostMapping("/me/favorites")
    public ResponseEntity<FavoriteResponse> addFavorite(
            @Valid @RequestBody FavoriteRequest request) {
        FavoriteResponse favoriteResponse = favoriteService.createFavorite(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{bookId}")
                .buildAndExpand(favoriteResponse.getBookId()).toUri();
        return ResponseEntity.created(location).body(favoriteResponse);
    }

    @DeleteMapping("/me/favorites/{bookId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable String bookId) {
        favoriteService.deleteFavorite(bookId);
        return ResponseEntity.noContent().build();
    }
}
