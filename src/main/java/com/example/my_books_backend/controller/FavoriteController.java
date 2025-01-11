package com.example.my_books_backend.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.example.my_books_backend.dto.favorite.CreateFavoriteRequest;
import com.example.my_books_backend.dto.favorite.FavoriteResponse;
import com.example.my_books_backend.service.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    @GetMapping("")
    public ResponseEntity<List<FavoriteResponse>> getAllFavorites() {
        List<FavoriteResponse> favorites = favoriteService.getAllFavorites();
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FavoriteResponse> getFavoriteById(@PathVariable Long id) {
        FavoriteResponse favorite = favoriteService.getFavoriteById(id);
        return ResponseEntity.ok(favorite);
    }

    @PostMapping("")
    public ResponseEntity<FavoriteResponse> createFavorite(
            @Valid @RequestBody CreateFavoriteRequest request) {
        FavoriteResponse favorite = favoriteService.createFavorite(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(favorite.getId()).toUri();
        return ResponseEntity.created(location).body(favorite);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable Long id) {
        favoriteService.deleteFavorite(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FavoriteResponse>> getFavoritesByUserId(@PathVariable Long userId) {
        List<FavoriteResponse> favorites = favoriteService.getFavoritesByUserId(userId);
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<FavoriteResponse>> getFavoritesByBookId(
            @PathVariable String bookId) {
        List<FavoriteResponse> favorites = favoriteService.getFavoritesByBookId(bookId);
        return ResponseEntity.ok(favorites);
    }
}
