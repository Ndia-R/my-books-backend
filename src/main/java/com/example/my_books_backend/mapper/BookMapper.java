package com.example.my_books_backend.mapper;

import java.util.Arrays;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.dto.book.BookResponse;
import com.example.my_books_backend.dto.book.BookDetailsResponse;
import com.example.my_books_backend.dto.book.BookPageResponse;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.Review;
import com.example.my_books_backend.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookMapper {
    private final ModelMapper modelMapper;
    private final ReviewRepository reviewRepository;

    public BookResponse toBookResponse(Book book) {
        BookResponse bookResponse = modelMapper.map(book, BookResponse.class);

        List<Long> genres = book.getGenres().stream().map(genre -> genre.getId()).toList();
        bookResponse.setGenreIds(genres);

        List<String> authors = Arrays.asList(book.getAuthors().split(","));
        bookResponse.setAuthors(authors);

        List<Review> reviews = reviewRepository.findByBookIdAndIsDeletedFalse(book.getId());
        Double averageRating =
                reviews.stream().mapToDouble(Review::getRating).average().orElse(0.0);
        bookResponse.setReviewCount(reviews.size());
        bookResponse.setAverageRating(averageRating);

        return bookResponse;
    }

    public List<BookResponse> toBookResponseList(List<Book> books) {
        return books.stream().map(book -> toBookResponse(book)).toList();
    }

    public BookPageResponse toBookPageResponse(Page<Book> bookPage) {
        Integer page = bookPage.getNumber();
        Integer totalPages = bookPage.getTotalPages();
        Integer totalItems = (int) bookPage.getTotalElements();
        List<BookResponse> bookResponses = toBookResponseList(bookPage.getContent());
        return new BookPageResponse(page, totalPages, totalItems, bookResponses);
    }

    public BookDetailsResponse toBookDetailsResponse(Book book) {
        BookDetailsResponse bookDetailsResponse = modelMapper.map(book, BookDetailsResponse.class);

        List<String> authors = Arrays.asList(book.getAuthors().split(","));
        bookDetailsResponse.setAuthors(authors);

        List<Review> reviews = reviewRepository.findByBookIdAndIsDeletedFalse(book.getId());
        Double averageRating =
                reviews.stream().mapToDouble(Review::getRating).average().orElse(0.0);
        bookDetailsResponse.setReviewCount(reviews.size());
        bookDetailsResponse.setAverageRating(averageRating);

        return bookDetailsResponse;
    }
}
