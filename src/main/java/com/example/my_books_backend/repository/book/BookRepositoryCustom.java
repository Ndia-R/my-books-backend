package com.example.my_books_backend.repository.book;

import java.util.List;
import com.example.my_books_backend.entity.Book;

public interface BookRepositoryCustom {
    // タイトル検索（カーソルベース）
    List<Book> findBooksByTitleKeywordWithCursor(String keyword, String cursor, int limit,
            String sortField, String sortDirection);
}
