package com.example.my_books_backend.mapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.dto.book.BookDto;
import com.example.my_books_backend.dto.book.BookResponseDto;
import com.example.my_books_backend.model.Book;

@Component
public class BookMapper {

    @Autowired
    private ModelMapper modelMapper;

    public BookDto toDto(Book book) {
        BookDto bookDto = modelMapper.map(book, BookDto.class);

        List<Integer> genreIds =
                Arrays.stream(book.getGenreIds().split(",")).map(Integer::parseInt).toList();
        bookDto.setGenreIds(genreIds);

        List<String> authors = Arrays.asList(book.getAuthors().split(","));
        bookDto.setAuthors(authors);

        return bookDto;
    }

    public Book toEntity(BookDto bookDto) {
        Book book = modelMapper.map(bookDto, Book.class);

        String genreIds = bookDto.getGenreIds().stream().map(String::valueOf)
                .collect(Collectors.joining(","));
        book.setGenreIds(genreIds);

        String authors = String.join(",", bookDto.getAuthors());
        book.setAuthors(authors);

        return book;
    }

    public List<BookDto> toDtoList(List<Book> books) {
        return books.stream().map(book -> toDto(book)).toList();
    }

    public BookResponseDto toResponseDto(Page<Book> pageBook) {
        Integer page = pageBook.getNumber();
        Integer totalPages = pageBook.getTotalPages();
        Integer totalItems = (int) pageBook.getTotalElements();
        List<BookDto> booksDto = toDtoList(pageBook.getContent());
        return new BookResponseDto(page, totalPages, totalItems, booksDto);
    }
}
