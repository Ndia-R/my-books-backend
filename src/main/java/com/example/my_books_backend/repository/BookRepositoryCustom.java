package com.example.my_books_backend.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.my_books_backend.model.Book;

public interface BookRepositoryCustom {
    Page<Book> findByGenreIds(List<String> genreIds, Pageable pageable);
}
