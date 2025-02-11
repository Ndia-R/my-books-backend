package com.example.my_books_backend.mapper;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.dto.favorite.FavoritePageResponse;
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
        FavoriteResponse favoriteResponse = modelMapper.map(favorite, FavoriteResponse.class);
        Book book = modelMapper.map(favorite.getBook(), Book.class);
        favoriteResponse.setBook(bookMapper.toBookResponse(book));
        return favoriteResponse;
    }

    public List<FavoriteResponse> toFavoriteResponseList(List<Favorite> favorites) {
        return favorites.stream().map(favorite -> toFavoriteResponse(favorite)).toList();
    }

    public FavoritePageResponse toFavoritePageResponse(Page<Favorite> favoritePage) {
        Integer page = favoritePage.getNumber();
        Integer totalPages = favoritePage.getTotalPages();
        Integer totalItems = (int) favoritePage.getTotalElements();
        List<FavoriteResponse> favoriteResponses =
                toFavoriteResponseList(favoritePage.getContent());
        return new FavoritePageResponse(page, totalPages, totalItems, favoriteResponses);
    }
}
