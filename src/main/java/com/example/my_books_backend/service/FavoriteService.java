package com.example.my_books_backend.service;

import com.example.my_books_backend.dto.favorite.FavoriteRequest;
import com.example.my_books_backend.dto.favorite.FavoriteResponse;
import com.example.my_books_backend.dto.favorite.FavoriteCountResponse;
import com.example.my_books_backend.dto.favorite.FavoritePageResponse;

public interface FavoriteService {
    FavoriteResponse getFavoriteByBookId(String bookId);

    FavoritePageResponse getFavorites(Integer page, Integer maxResults);

    FavoriteCountResponse getFavoriteCount(String bookId);

    FavoriteResponse createFavorite(FavoriteRequest request);

    void deleteFavorite(String bookId);
}
