package com.example.my_books_backend.service;

import com.example.my_books_backend.dto.favorite.FavoriteRequest;
import com.example.my_books_backend.dto.favorite.FavoriteResponse;
import com.example.my_books_backend.entity.User;
import com.example.my_books_backend.dto.favorite.FavoriteInfoResponse;
import com.example.my_books_backend.dto.favorite.FavoritePageResponse;

public interface FavoriteService {
    FavoritePageResponse getFavoritePageByUser(Integer page, Integer maxResults, User user);

    FavoriteResponse getFavoriteByBookId(String bookId, User user);

    FavoriteInfoResponse getFavoriteInfo(String bookId, Long userId);

    FavoriteResponse createFavorite(FavoriteRequest request, User user);

    void deleteFavorite(String bookId, User user);
}
