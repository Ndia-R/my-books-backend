package com.example.my_books_backend.mapper;

import java.util.Arrays;
import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.dto.book.BookResponse;
import com.example.my_books_backend.dto.CursorPageResponse;
import com.example.my_books_backend.dto.PageResponse;
import com.example.my_books_backend.dto.book.BookDetailsResponse;
import com.example.my_books_backend.entity.Book;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookMapper {
    private final ModelMapper modelMapper;

    public BookResponse toBookResponse(Book book) {
        BookResponse response = modelMapper.map(book, BookResponse.class);

        List<Long> genres = book.getGenres().stream().map(genre -> genre.getId()).toList();
        response.setGenreIds(genres);

        List<String> authors = Arrays.asList(book.getAuthors().split(","));
        response.setAuthors(authors);

        return response;
    }

    public List<BookResponse> toBookResponseList(List<Book> books) {
        return books.stream().map(book -> toBookResponse(book)).toList();
    }

    public PageResponse<BookResponse> toPageResponse(Page<Book> books) {
        List<BookResponse> responses = toBookResponseList(books.getContent());
        // Pageableの内部的にはデフォルトで0ベースだが、エンドポイントとしては1ベースなので+1する
        return new PageResponse<BookResponse>(books.getNumber() + 1, books.getSize(),
                books.getTotalPages(), books.getTotalElements(), books.hasNext(),
                books.hasPrevious(), responses);
    }

    public CursorPageResponse<BookResponse> toCursorPageResponse(List<Book> books, Integer limit) {
        Boolean hasNext = books.size() > limit;
        if (hasNext) {
            books = books.subList(0, limit); // 余分な1件を削除
        }
        String endCursor = hasNext ? books.get(books.size() - 1).getId() : null;
        List<BookResponse> responses = toBookResponseList(books);
        return new CursorPageResponse<BookResponse>(endCursor, hasNext, responses);
    }

    public BookDetailsResponse toBookDetailsResponse(Book book) {
        BookDetailsResponse response = modelMapper.map(book, BookDetailsResponse.class);

        List<String> authors = Arrays.asList(book.getAuthors().split(","));
        response.setAuthors(authors);

        return response;
    }
}
