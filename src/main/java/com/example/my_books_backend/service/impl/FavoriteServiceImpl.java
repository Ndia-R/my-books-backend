package com.example.my_books_backend.service.impl;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.my_books_backend.dto.favorite.FavoriteRequest;
import com.example.my_books_backend.dto.favorite.FavoriteResponse;
import com.example.my_books_backend.dto.favorite.FavoriteCountsResponse;
import com.example.my_books_backend.dto.favorite.FavoritePageResponse;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.Favorite;
import com.example.my_books_backend.entity.FavoriteId;
import com.example.my_books_backend.entity.User;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.mapper.FavoriteMapper;
import com.example.my_books_backend.repository.BookRepository;
import com.example.my_books_backend.repository.FavoriteRepository;
import com.example.my_books_backend.service.FavoriteService;
import com.example.my_books_backend.util.PaginationUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final FavoriteMapper favoriteMapper;

    private final BookRepository bookRepository;
    private final PaginationUtil paginationUtil;

    /** ユーザーが追加したすべてのお気に入り情報のデフォルトソート（作成日） */
    private static final Sort DEFAULT_SORT =
            Sort.by(Sort.Order.desc("createdAt"), Sort.Order.asc("id"));

    /**
     * {@inheritDoc}
     */
    @Override
    public FavoriteResponse getUserFavoriteForBook(String bookId, User user) {
        FavoriteId favoriteId = new FavoriteId(user.getId(), bookId);
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new NotFoundException("Favorite not found"));
        return favoriteMapper.toFavoriteResponse(favorite);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FavoritePageResponse getUserFavorites(Integer page, Integer maxResults, User user) {
        Pageable pageable = paginationUtil.createPageable(page, maxResults, DEFAULT_SORT);
        Page<Favorite> favorites = favoriteRepository.findByUser(user, pageable);
        return favoriteMapper.toFavoritePageResponse(favorites);
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
        FavoriteId favoriteId = new FavoriteId(user.getId(), request.getBookId());

        Favorite favorite = new Favorite();
        favorite.setId(favoriteId);
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
    public void deleteFavorite(String bookId, User user) {
        FavoriteId favoriteId = new FavoriteId(user.getId(), bookId);
        favoriteRepository.deleteById(favoriteId);
    }
}
