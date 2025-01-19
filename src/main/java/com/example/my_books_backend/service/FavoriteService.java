package com.example.my_books_backend.service;

import com.example.my_books_backend.dto.book.PaginatedBookResponse;
import com.example.my_books_backend.dto.favorite.FavoriteCountResponse;
import com.example.my_books_backend.dto.favorite.FavoriteRequest;
import com.example.my_books_backend.dto.favorite.FavoriteResponse;
import com.example.my_books_backend.dto.favorite.FavoriteStateResponse;

public interface FavoriteService {
    PaginatedBookResponse getFavorites(Integer page, Integer maxResults);

    FavoriteResponse addFavorite(FavoriteRequest request);

    void removeFavorite(String bookId);

    FavoriteStateResponse getFavoriteState(String bookId);

    FavoriteCountResponse getFavoriteCount(String bookId);
}
