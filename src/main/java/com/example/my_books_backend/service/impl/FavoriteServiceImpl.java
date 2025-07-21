package com.example.my_books_backend.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.my_books_backend.dto.favorite.FavoriteRequest;
import com.example.my_books_backend.dto.favorite.FavoriteResponse;
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
import com.example.my_books_backend.util.PageableUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FavoriteServiceImpl implements FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final FavoriteMapper favoriteMapper;

    private final BookRepository bookRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public PageResponse<FavoriteResponse> getUserFavorites(
        User user,
        Long page,
        Long size,
        String sortString,
        String bookId
    ) {
        Pageable pageable = PageableUtils.createPageable(
            page,
            size,
            sortString,
            PageableUtils.FAVORITE_ALLOWED_FIELDS
        );
        Page<Favorite> pageObj = (bookId == null)
            ? favoriteRepository.findByUserAndIsDeletedFalse(user, pageable)
            : favoriteRepository.findByUserAndIsDeletedFalseAndBookId(user, pageable, bookId);

        // 2クエリ戦略：IDリストから関連データを含むリストを取得
        List<Long> ids = pageObj.getContent().stream().map(Favorite::getId).toList();
        List<Favorite> list = favoriteRepository.findAllByIdInWithRelations(ids);

        // ソート順序を復元
        List<Favorite> sortedList = PageableUtils.restoreSortOrder(ids, list, Favorite::getId);

        // 元のページネーション情報を保持して新しいPageオブジェクトを作成
        Page<Favorite> updatedPageObj = new PageImpl<>(
            sortedList,
            pageable,
            pageObj.getTotalElements()
        );

        return favoriteMapper.toPageResponse(updatedPageObj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FavoriteCountsResponse getBookFavoriteCounts(String bookId) {
        Long count = favoriteRepository.countByBookIdAndIsDeletedFalse(bookId);

        FavoriteCountsResponse response = new FavoriteCountsResponse();
        response.setBookId(bookId);
        response.setFavoriteCount(count);

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
