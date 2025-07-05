package com.example.my_books_backend.mapper;

import java.util.Arrays;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import com.example.my_books_backend.dto.book.BookResponse;
import com.example.my_books_backend.dto.PageResponse;
import com.example.my_books_backend.dto.SliceResponse;
import com.example.my_books_backend.dto.book.BookDetailsResponse;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.Genre;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "genreIds", source = "genres", qualifiedByName = "genresToIds")
    @Mapping(target = "authors", source = "authors", qualifiedByName = "splitAuthors")
    BookResponse toBookResponse(Book book);

    List<BookResponse> toBookResponseList(List<Book> books);

    @Mapping(target = "authors", source = "authors", qualifiedByName = "splitAuthors")
    BookDetailsResponse toBookDetailsResponse(Book book);

    @Named("genresToIds")
    default List<Long> genresToIds(List<Genre> genres) {
        return genres.stream().map(Genre::getId).toList();
    }

    @Named("splitAuthors")
    default List<String> splitAuthors(String authors) {
        return Arrays.asList(authors.split(","));
    }

    default PageResponse<BookResponse> toPageResponse(Page<Book> books) {
        List<BookResponse> responses = toBookResponseList(books.getContent());
        // Pageableの内部的にはデフォルトで0ベースだが、エンドポイントとしては1ベースなので+1する
        return new PageResponse<BookResponse>(
            books.getNumber() + 1,
            books.getSize(),
            books.getTotalPages(),
            books.getTotalElements(),
            books.hasNext(),
            books.hasPrevious(),
            responses
        );
    }

    default SliceResponse<BookResponse> toSliceResponse(Slice<Book> books) {
        List<BookResponse> responses = toBookResponseList(books.getContent());
        // Pageableの内部的にはデフォルトで0ベースだが、エンドポイントとしては1ベースなので+1する
        return new SliceResponse<BookResponse>(
            books.getNumber() + 1,
            books.getSize(),
            books.hasNext(),
            responses
        );
    }
}
