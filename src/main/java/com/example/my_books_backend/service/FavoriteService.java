package com.example.my_books_backend.service;

import com.example.my_books_backend.dto.favorite.FavoriteRequest;
import com.example.my_books_backend.dto.favorite.FavoriteResponse;
import com.example.my_books_backend.dto.favorite.FavoriteInfoResponse;
import com.example.my_books_backend.dto.favorite.FavoritePageResponse;

public interface FavoriteService {
    FavoriteResponse getFavoriteById(String bookId);

    FavoritePageResponse getFavoritePage(Integer page, Integer maxResults);

    FavoriteInfoResponse getFavoriteInfo(String bookId, Long userId);

    FavoriteResponse createFavorite(FavoriteRequest request);

    void deleteFavorite(String bookId);
}
