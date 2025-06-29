package com.example.my_books_backend.repository.book;

import java.util.List;
import com.example.my_books_backend.entity.Book;

public interface BookRepositoryCustom {
    // タイトル検索（カーソルベース）
    List<Book> findBooksByTitleKeywordWithCursor(
        String keyword,
        String cursor,
        int limit,
        String sortString
    );

    // ジャンル検索 OR条件（カーソルベース）
    List<Book> findBooksByGenresOrWithCursor(
        List<Long> genreIds,
        String cursor,
        int limit,
        String sortString
    );

    // ジャンル検索 AND条件（カーソルベース）
    List<Book> findBooksByGenresAndWithCursor(
        List<Long> genreIds,
        String cursor,
        int limit,
        String sortString
    );
}
