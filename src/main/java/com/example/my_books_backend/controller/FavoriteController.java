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
import com.example.my_books_backend.dto.book.PaginatedBookResponse;
import com.example.my_books_backend.dto.favorite.FavoriteRequest;
import com.example.my_books_backend.dto.favorite.FavoriteResponse;
import com.example.my_books_backend.dto.favorite.FavoriteStateResponse;
import com.example.my_books_backend.service.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    @GetMapping("")
    public ResponseEntity<PaginatedBookResponse> getFavorites(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer maxResults) {
        PaginatedBookResponse favorites = favoriteService.getFavorites(page, maxResults);
        return ResponseEntity.ok(favorites);
    }

    @PostMapping("")
    public ResponseEntity<FavoriteResponse> addFavorite(
            @Valid @RequestBody FavoriteRequest request) {
        FavoriteResponse favorite = favoriteService.addFavorite(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(favorite.getFavoriteId()).toUri();
        return ResponseEntity.created(location).body(favorite);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable String bookId) {
        favoriteService.removeFavorite(bookId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/favorite-state/{bookId}")
    public ResponseEntity<FavoriteStateResponse> getFavoriteState(@PathVariable String bookId) {
        FavoriteStateResponse favoriteStateResponse = favoriteService.getFavoriteState(bookId);
        return ResponseEntity.ok(favoriteStateResponse);
    }
}
