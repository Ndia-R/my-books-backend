package com.example.my_books_backend.mapper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.dto.book.BookResponse;
import com.example.my_books_backend.dto.CursorPageResponse;
import com.example.my_books_backend.dto.book.BookDetailsResponse;
import com.example.my_books_backend.dto.book.BookPageResponse;
import com.example.my_books_backend.entity.Book;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookMapper {
    private final ModelMapper modelMapper;

    /**
     * 単一の書籍をBookResponseに変換（レビュー情報含む）
     */
    public BookResponse toBookResponse(Book book, Integer reviewCount, Double averageRating) {
        BookResponse response = modelMapper.map(book, BookResponse.class);

        List<Long> genres = book.getGenres().stream().map(genre -> genre.getId()).toList();
        response.setGenreIds(genres);

        List<String> authors = Arrays.asList(book.getAuthors().split(","));
        response.setAuthors(authors);

        response.setReviewCount(reviewCount);
        response.setAverageRating(averageRating);

        return response;
    }

    /**
     * 複数の書籍をBookResponseリストに変換（レビュー情報含む）
     */
    public List<BookResponse> toBookResponseList(List<Book> books,
            Map<String, Integer> reviewCounts, Map<String, Double> averageRatings) {
        return books.stream()
                .map(book -> toBookResponse(book, reviewCounts.getOrDefault(book.getId(), 0),
                        averageRatings.getOrDefault(book.getId(), 0.0)))
                .toList();
    }

    /**
     * PageオブジェクトからBookPageResponseに変換
     */
    public BookPageResponse toBookPageResponse(Page<Book> books, Map<String, Integer> reviewCounts,
            Map<String, Double> averageRatings) {
        Integer page = books.getNumber();
        Integer totalPages = books.getTotalPages();
        Integer totalItems = (int) books.getTotalElements();
        List<BookResponse> responses =
                toBookResponseList(books.getContent(), reviewCounts, averageRatings);
        return new BookPageResponse(page, totalPages, totalItems, responses);
    }

    /**
     * カーソルページレスポンスに変換
     */
    public CursorPageResponse<BookResponse> toCursorPageResponse(List<Book> books, String endCursor,
            Boolean hasNext, Map<String, Integer> reviewCounts,
            Map<String, Double> averageRatings) {
        List<BookResponse> responses = toBookResponseList(books, reviewCounts, averageRatings);
        return new CursorPageResponse<>(endCursor, hasNext, responses);
    }

    /**
     * 書籍詳細レスポンスに変換
     */
    public BookDetailsResponse toBookDetailsResponse(Book book, Integer reviewCount,
            Double averageRating) {
        BookDetailsResponse response = modelMapper.map(book, BookDetailsResponse.class);

        List<String> authors = Arrays.asList(book.getAuthors().split(","));
        response.setAuthors(authors);

        response.setReviewCount(reviewCount);
        response.setAverageRating(averageRating);

        return response;
    }
}
