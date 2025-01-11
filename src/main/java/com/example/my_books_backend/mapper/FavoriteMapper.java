package com.example.my_books_backend.mapper;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.dto.favorite.CreateFavoriteRequest;
import com.example.my_books_backend.dto.favorite.FavoriteResponse;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.Favorite;
import com.example.my_books_backend.entity.User;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.repository.BookRepository;
import com.example.my_books_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FavoriteMapper {
    private final ModelMapper modelMapper;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public FavoriteResponse toFavoriteResponse(Favorite favorite) {
        FavoriteResponse favoriteResponse = modelMapper.map(favorite, FavoriteResponse.class);
        User user = modelMapper.map(favorite.getUser(), User.class);
        Book book = modelMapper.map(favorite.getBook(), Book.class);
        favoriteResponse.setUser(userMapper.toSimpleUserInfo(user));
        favoriteResponse.setBook(bookMapper.toBookResponse(book));
        return favoriteResponse;
    }

    public List<FavoriteResponse> toFavoriteResponseList(List<Favorite> favorites) {
        return favorites.stream().map(favorite -> toFavoriteResponse(favorite)).toList();
    }

    public Favorite toFavoriteEntity(CreateFavoriteRequest createFavoriteRequest) {
        Favorite favorite = new Favorite();
        User user = userRepository.findById(createFavoriteRequest.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        Book book = bookRepository.findById(createFavoriteRequest.getBookId())
                .orElseThrow(() -> new NotFoundException("Book not found"));
        favorite.setUser(user);
        favorite.setBook(book);
        return favorite;
    }
}
