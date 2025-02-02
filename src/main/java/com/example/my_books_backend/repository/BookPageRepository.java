package com.example.my_books_backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.my_books_backend.entity.BookPage;
import com.example.my_books_backend.entity.BookPageId;

@Repository
public interface BookPageRepository extends JpaRepository<BookPage, BookPageId> {
    List<BookPage> findByBookId(String bookId);
}
