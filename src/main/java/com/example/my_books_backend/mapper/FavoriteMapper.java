package com.example.my_books_backend.mapper;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.dto.CursorPageResponse;
import com.example.my_books_backend.dto.PageResponse;
import com.example.my_books_backend.dto.favorite.FavoriteResponse;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.Favorite;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FavoriteMapper {
    private final ModelMapper modelMapper;
    private final BookMapper bookMapper;

    public FavoriteResponse toFavoriteResponse(Favorite favorite) {
        FavoriteResponse response = modelMapper.map(favorite, FavoriteResponse.class);
        Book book = modelMapper.map(favorite.getBook(), Book.class);
        response.setBook(bookMapper.toBookResponse(book));
        return response;
    }

    public List<FavoriteResponse> toFavoriteResponseList(List<Favorite> favorites) {
        return favorites.stream().map(favorite -> toFavoriteResponse(favorite)).toList();
    }

    public PageResponse<FavoriteResponse> toPageResponse(Page<Favorite> favorites) {
        List<FavoriteResponse> responses = toFavoriteResponseList(favorites.getContent());
        // Pageableの内部的にはデフォルトで0ベースだが、エンドポイントとしては1ベースなので+1する
        return new PageResponse<FavoriteResponse>(favorites.getNumber() + 1, favorites.getSize(),
                favorites.getTotalPages(), favorites.getTotalElements(), favorites.hasNext(),
                favorites.hasPrevious(), responses);
    }

    public CursorPageResponse<FavoriteResponse> toCursorPageResponse(List<Favorite> reviews,
            Integer limit) {
        Boolean hasNext = reviews.size() > limit;
        if (hasNext) {
            reviews = reviews.subList(0, limit); // 余分な1件を削除
        }
        String endCursor = hasNext ? reviews.get(reviews.size() - 1).getId().toString() : null;
        List<FavoriteResponse> responses = toFavoriteResponseList(reviews);
        return new CursorPageResponse<FavoriteResponse>(endCursor, hasNext, responses);
    }
}
