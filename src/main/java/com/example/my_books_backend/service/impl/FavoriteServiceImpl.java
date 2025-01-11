package com.example.my_books_backend.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.my_books_backend.dto.favorite.CreateFavoriteRequest;
import com.example.my_books_backend.dto.favorite.FavoriteResponse;
import com.example.my_books_backend.entity.Favorite;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.mapper.FavoriteMapper;
import com.example.my_books_backend.repository.FavoriteRepository;
import com.example.my_books_backend.service.FavoriteService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final FavoriteMapper favoriteMapper;

    @Override
    public List<FavoriteResponse> getAllFavorites() {
        List<Favorite> favorites = favoriteRepository.findAll();
        return favoriteMapper.toFavoriteResponseList(favorites);
    }

    @Override
    public FavoriteResponse getFavoriteById(Long id) {
        Favorite favorite = findFavoriteById(id);
        return favoriteMapper.toFavoriteResponse(favorite);
    }

    @Override
    public FavoriteResponse createFavorite(CreateFavoriteRequest request) {
        Favorite favorite = favoriteMapper.toFavoriteEntity(request);
        Favorite savedFavorite = favoriteRepository.save(favorite);
        return favoriteMapper.toFavoriteResponse(savedFavorite);
    }

    @Override
    public void deleteFavorite(Long id) {
        Favorite favorite = findFavoriteById(id);
        favoriteRepository.delete(favorite);
    }

    @Override
    public List<FavoriteResponse> getFavoritesByUserId(Long userId) {
        List<Favorite> favorites = favoriteRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        return favoriteMapper.toFavoriteResponseList(favorites);
    }

    @Override
    public List<FavoriteResponse> getFavoritesByBookId(String bookId) {
        List<Favorite> favorites = favoriteRepository.findByBookIdOrderByUpdatedAtDesc(bookId);
        return favoriteMapper.toFavoriteResponseList(favorites);
    }

    private Favorite findFavoriteById(Long id) {
        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("見つかりませんでした。 ID: " + id));
        return favorite;
    }
}
