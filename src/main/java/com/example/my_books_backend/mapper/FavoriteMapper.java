package com.example.my_books_backend.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import com.example.my_books_backend.dto.CursorPageResponse;
import com.example.my_books_backend.dto.PageResponse;
import com.example.my_books_backend.dto.favorite.FavoriteResponse;
import com.example.my_books_backend.entity.Favorite;

@Mapper(componentModel = "spring")
public abstract class FavoriteMapper {

    @Autowired
    protected BookMapper bookMapper;

    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "book", expression = "java(bookMapper.toBookResponse(favorite.getBook()))")
    public abstract FavoriteResponse toFavoriteResponse(Favorite favorite);

    public abstract List<FavoriteResponse> toFavoriteResponseList(List<Favorite> favorites);

    public PageResponse<FavoriteResponse> toPageResponse(Page<Favorite> favorites) {
        List<FavoriteResponse> responses = toFavoriteResponseList(favorites.getContent());
        // Pageableの内部的にはデフォルトで0ベースだが、エンドポイントとしては1ベースなので+1する
        return new PageResponse<FavoriteResponse>(favorites.getNumber() + 1, favorites.getSize(),
                favorites.getTotalPages(), favorites.getTotalElements(), favorites.hasNext(),
                favorites.hasPrevious(), responses);
    }

    public CursorPageResponse<FavoriteResponse> toCursorPageResponse(List<Favorite> favorites,
            Integer limit) {
        Boolean hasNext = favorites.size() > limit;
        if (hasNext) {
            favorites = favorites.subList(0, limit); // 余分な1件を削除
        }
        String endCursor = hasNext ? favorites.get(favorites.size() - 1).getId().toString() : null;
        List<FavoriteResponse> responses = toFavoriteResponseList(favorites);
        return new CursorPageResponse<FavoriteResponse>(endCursor, hasNext, responses);
    }
}
