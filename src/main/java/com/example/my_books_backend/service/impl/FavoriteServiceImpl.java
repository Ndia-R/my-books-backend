package com.example.my_books_backend.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.my_books_backend.dto.favorite.FavoriteRequest;
import com.example.my_books_backend.dto.favorite.FavoriteResponse;
import com.example.my_books_backend.dto.CursorPageResponse;
import com.example.my_books_backend.dto.PageResponse;
import com.example.my_books_backend.dto.favorite.FavoriteCountsResponse;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.Favorite;
import com.example.my_books_backend.entity.User;
import com.example.my_books_backend.exception.ConflictException;
import com.example.my_books_backend.exception.ForbiddenException;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.mapper.FavoriteMapper;
import com.example.my_books_backend.repository.BookRepository;
import com.example.my_books_backend.repository.FavoriteRepository;
import com.example.my_books_backend.service.FavoriteService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final FavoriteMapper favoriteMapper;

    private final BookRepository bookRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public PageResponse<FavoriteResponse> getUserFavorites(User user, Pageable pageable,
            String bookId) {
        Page<Favorite> favorites = (bookId == null)
                ? favoriteRepository.findByUserAndIsDeletedFalse(user, pageable)
                : favoriteRepository.findByUserAndIsDeletedFalseAndBookId(user, pageable, bookId);
        return favoriteMapper.toPageResponse(favorites);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CursorPageResponse<FavoriteResponse> getUserFavoritesWithCursor(User user, String cursor,
            Integer limit) {
        // 次のページの有無を判定するために、1件多く取得
        List<Favorite> favorites = favoriteRepository.findFavoritesByUserIdWithCursor(user.getId(),
                (cursor != null) ? Long.parseLong(cursor) : null, limit + 1);
        return favoriteMapper.toCursorPageResponse(favorites, limit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FavoriteCountsResponse getBookFavoriteCounts(String bookId) {
        List<Favorite> favorites = favoriteRepository.findByBookId(bookId);

        FavoriteCountsResponse response = new FavoriteCountsResponse();
        response.setBookId(bookId);
        response.setFavoriteCount(favorites.size());

        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public FavoriteResponse createFavorite(FavoriteRequest request, User user) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new NotFoundException("Book not found"));

        Optional<Favorite> existingFavorite = favoriteRepository.findByUserAndBook(user, book);

        Favorite favorite = new Favorite();
        if (existingFavorite.isPresent()) {
            favorite = existingFavorite.get();
            if (favorite.getIsDeleted()) {
                favorite.setIsDeleted(false);
            } else {
                throw new ConflictException("すでにこの書籍にはお気に入りが登録されています。");
            }
        }
        favorite.setUser(user);
        favorite.setBook(book);

        Favorite savedFavorite = favoriteRepository.save(favorite);
        return favoriteMapper.toFavoriteResponse(savedFavorite);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteFavorite(Long id, User user) {
        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("favorite not found"));

        if (!favorite.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("このお気に入りを削除する権限がありません");
        }

        favorite.setIsDeleted(true);
        favoriteRepository.save(favorite);
    }
}
