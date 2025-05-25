package com.example.my_books_backend.mapper;

import java.util.Arrays;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.dto.book.BookResponse;
import com.example.my_books_backend.dto.book.BookCursorResponse;
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
        BookResponse response = modelMapper.map(book, BookResponse.class);

        List<Long> genres = book.getGenres().stream().map(genre -> genre.getId()).toList();
        response.setGenreIds(genres);

        List<String> authors = Arrays.asList(book.getAuthors().split(","));
        response.setAuthors(authors);

        List<Review> reviews = reviewRepository.findByBookIdAndIsDeletedFalse(book.getId());
        Double averageRating =
                reviews.stream().mapToDouble(Review::getRating).average().orElse(0.0);
        response.setReviewCount(reviews.size());
        response.setAverageRating(averageRating);

        return response;
    }

    public List<BookResponse> toBookResponseList(List<Book> books) {
        return books.stream().map(book -> toBookResponse(book)).toList();
    }

    public BookPageResponse toBookPageResponse(Page<Book> books) {
        Integer page = books.getNumber();
        Integer totalPages = books.getTotalPages();
        Integer totalItems = (int) books.getTotalElements();
        List<BookResponse> responses = toBookResponseList(books.getContent());
        return new BookPageResponse(page, totalPages, totalItems, responses);
    }

    public BookCursorResponse toBookCursorResponse(List<Book> books, Integer limit) {
        Boolean hasNext = books.size() > limit;
        if (hasNext) {
            books = books.subList(0, limit); // 余分な1件を削除
        }
        String endCursor = hasNext ? books.get(books.size() - 1).getId() : null;
        List<BookResponse> responses = toBookResponseList(books);
        return new BookCursorResponse(hasNext, endCursor, responses);
    }

    public BookDetailsResponse toBookDetailsResponse(Book book) {
        BookDetailsResponse response = modelMapper.map(book, BookDetailsResponse.class);

        List<String> authors = Arrays.asList(book.getAuthors().split(","));
        response.setAuthors(authors);

        List<Review> reviews = reviewRepository.findByBookIdAndIsDeletedFalse(book.getId());
        Double averageRating =
                reviews.stream().mapToDouble(Review::getRating).average().orElse(0.0);
        response.setReviewCount(reviews.size());
        response.setAverageRating(averageRating);

        return response;
    }
}
