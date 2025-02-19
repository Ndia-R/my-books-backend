package com.example.my_books_backend.service.impl;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.my_books_backend.dto.favorite.FavoriteRequest;
import com.example.my_books_backend.dto.favorite.FavoriteResponse;
import com.example.my_books_backend.dto.favorite.FavoriteInfoResponse;
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

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "updatedAt");

    @Override
    public FavoritePageResponse getFavoritePageByUser(Integer page, Integer maxResults, User user) {
        Pageable pageable = paginationUtil.createPageable(page, maxResults, DEFAULT_SORT);
        Page<Favorite> favoritePage = favoriteRepository.findByUser(user, pageable);
        return favoriteMapper.toFavoritePageResponse(favoritePage);
    }

    @Override
    public FavoriteResponse getFavoriteByBookId(String bookId, User user) {
        FavoriteId favoriteId = new FavoriteId(user.getId(), bookId);
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new NotFoundException("Favorite not found"));
        return favoriteMapper.toFavoriteResponse(favorite);
    }

    @Override
    public FavoriteInfoResponse getFavoriteInfo(String bookId, Long userId) {
        List<Favorite> favorites = favoriteRepository.findByBookId(bookId);

        boolean isFavorite = favorites.stream()
                .anyMatch(favorite -> favorite.getId().getUserId().equals(userId));

        FavoriteInfoResponse favoriteInfoResponse = new FavoriteInfoResponse();
        favoriteInfoResponse.setIsFavorite(isFavorite);
        favoriteInfoResponse.setFavoriteCount(favorites.size());

        return favoriteInfoResponse;
    }

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

    @Override
    @Transactional
    public void deleteFavorite(String bookId, User user) {
        FavoriteId favoriteId = new FavoriteId(user.getId(), bookId);
        favoriteRepository.deleteById(favoriteId);
    }
}
