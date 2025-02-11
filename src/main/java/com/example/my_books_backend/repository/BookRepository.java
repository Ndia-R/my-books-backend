package com.example.my_books_backend.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.my_books_backend.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, String> {
    List<Book> findTop10ByOrderByPublishedDateDesc();

    Page<Book> findByTitleContaining(String q, Pageable pageable);

    @Query("SELECT DISTINCT b FROM Book b JOIN b.genres g WHERE g.id IN :genreIds")
    Page<Book> findByGenreIds(@Param("genreIds") List<Long> genreIds, Pageable pageable);

    @Query("""
            SELECT b FROM Book b
            JOIN b.genres bg
            WHERE bg.id IN :genreIds
            GROUP BY b.id
            HAVING COUNT(DISTINCT bg.id) = :size
            """)
    Page<Book> findByAllGenreIds(@Param("genreIds") List<Long> genreIds, @Param("size") long size,
            Pageable pageable);
}
