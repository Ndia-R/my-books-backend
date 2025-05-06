package com.example.my_books_backend.controller;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.example.my_books_backend.dto.favorite.FavoriteRequest;
import com.example.my_books_backend.dto.favorite.FavoriteResponse;
import com.example.my_books_backend.entity.User;
import com.example.my_books_backend.service.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    // お気に入り追加
    @PostMapping("")
    public ResponseEntity<FavoriteResponse> createFavorite(
            @Valid @RequestBody FavoriteRequest request, @AuthenticationPrincipal User user) {
        FavoriteResponse response = favoriteService.createFavorite(request, user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{bookId}")
                .buildAndExpand(response.getBookId()).toUri();
        return ResponseEntity.created(location).body(response);
    }

    // お気に入り削除
    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable String bookId,
            @AuthenticationPrincipal User user) {
        favoriteService.deleteFavorite(bookId, user);
        return ResponseEntity.noContent().build();
    }
}
