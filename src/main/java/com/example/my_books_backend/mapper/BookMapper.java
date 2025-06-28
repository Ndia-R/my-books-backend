package com.example.my_books_backend.mapper;

import java.util.Arrays;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.data.domain.Page;
import com.example.my_books_backend.dto.book.BookResponse;
import com.example.my_books_backend.dto.CursorPageResponse;
import com.example.my_books_backend.dto.PageResponse;
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

    default CursorPageResponse<BookResponse> toCursorPageResponse(List<Book> books, Integer limit) {
        Boolean hasNext = books.size() > limit;
        if (hasNext) {
            books = books.subList(0, limit); // 余分な1件を削除
        }
        String endCursor = hasNext ? books.get(books.size() - 1).getId() : null;
        List<BookResponse> responses = toBookResponseList(books);
        return new CursorPageResponse<BookResponse>(endCursor, hasNext, responses);
    }
}
