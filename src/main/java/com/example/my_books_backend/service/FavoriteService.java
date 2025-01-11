package com.example.my_books_backend.service;

import java.util.List;
import com.example.my_books_backend.dto.favorite.CreateFavoriteRequest;
import com.example.my_books_backend.dto.favorite.FavoriteResponse;

public interface FavoriteService {
    List<FavoriteResponse> getAllFavorites();

    FavoriteResponse getFavoriteById(Long id);

    FavoriteResponse createFavorite(CreateFavoriteRequest request);

    void deleteFavorite(Long id);

    List<FavoriteResponse> getFavoritesByUserId(Long userId);

    List<FavoriteResponse> getFavoritesByBookId(String bookId);
}
