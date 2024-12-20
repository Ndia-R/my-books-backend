package com.example.my_books_backend.mapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.dto.book.BookResponse;
import com.example.my_books_backend.dto.book.PaginatedBookResponse;
import com.example.my_books_backend.entity.Book;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookMapper {
    private final ModelMapper modelMapper;

    public BookResponse toResponse(Book book) {
        BookResponse bookResponse = modelMapper.map(book, BookResponse.class);

        List<Integer> genreIds =
                Arrays.stream(book.getGenreIds().split(",")).map(Integer::parseInt).toList();
        bookResponse.setGenreIds(genreIds);

        List<String> authors = Arrays.asList(book.getAuthors().split(","));
        bookResponse.setAuthors(authors);

        return bookResponse;
    }

    public Book toEntity(BookResponse bookResponse) {
        Book book = modelMapper.map(bookResponse, Book.class);

        String genreIds = bookResponse.getGenreIds().stream().map(String::valueOf)
                .collect(Collectors.joining(","));
        book.setGenreIds(genreIds);

        String authors = String.join(",", bookResponse.getAuthors());
        book.setAuthors(authors);

        return book;
    }

    public List<BookResponse> toResponseList(List<Book> books) {
        return books.stream().map(book -> toResponse(book)).toList();
    }

    public PaginatedBookResponse toPaginatedBookResponse(Page<Book> pageBook) {
        Integer page = pageBook.getNumber();
        Integer totalPages = pageBook.getTotalPages();
        Integer totalItems = (int) pageBook.getTotalElements();
        List<BookResponse> booksDto = toResponseList(pageBook.getContent());
        return new PaginatedBookResponse(page, totalPages, totalItems, booksDto);
    }
}
