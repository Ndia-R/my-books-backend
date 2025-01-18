package com.example.my_books_backend.mapper;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.dto.book.BookResponse;
import com.example.my_books_backend.dto.book.PaginatedBookResponse;
import com.example.my_books_backend.dto.favorite.FavoriteResponse;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.Favorite;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FavoriteMapper {
    private final ModelMapper modelMapper;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;

    public FavoriteResponse toFavoriteResponse(Favorite favorite) {
        FavoriteResponse favoriteResponse = modelMapper.map(favorite, FavoriteResponse.class);
        favoriteResponse.setUser(userMapper.toSimpleUserInfo(favorite.getUser()));
        favoriteResponse.setBook(bookMapper.toBookResponse(favorite.getBook()));
        return favoriteResponse;
    }

    public List<FavoriteResponse> toFavoriteResponseList(List<Favorite> favorites) {
        return favorites.stream().map(favorite -> toFavoriteResponse(favorite)).toList();
    }

    public PaginatedBookResponse toPaginatedBookResponse(Page<Favorite> favorites) {
        Integer page = favorites.getNumber();
        Integer totalPages = favorites.getTotalPages();
        Integer totalItems = (int) favorites.getTotalElements();
        List<Book> books =
                favorites.getContent().stream().map(favorite -> favorite.getBook()).toList();
        List<BookResponse> booksDto = bookMapper.toBookResponseList(books);
        return new PaginatedBookResponse(page, totalPages, totalItems, booksDto);
    }
}
