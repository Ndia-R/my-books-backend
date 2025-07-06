package com.example.my_books_backend.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import com.example.my_books_backend.dto.PageResponse;
import com.example.my_books_backend.dto.SliceResponse;
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
        return new PageResponse<FavoriteResponse>(
            favorites.getNumber() + 1,
            favorites.getSize(),
            favorites.getTotalPages(),
            favorites.getTotalElements(),
            favorites.hasNext(),
            favorites.hasPrevious(),
            responses
        );
    }

    public SliceResponse<FavoriteResponse> toSliceResponse(Slice<Favorite> favorites) {
        List<FavoriteResponse> responses = toFavoriteResponseList(favorites.getContent());
        // Pageableの内部的にはデフォルトで0ベースだが、エンドポイントとしては1ベースなので+1する
        return new SliceResponse<FavoriteResponse>(
            favorites.getNumber() + 1,
            favorites.getSize(),
            favorites.hasNext(),
            responses
        );
    }
}
