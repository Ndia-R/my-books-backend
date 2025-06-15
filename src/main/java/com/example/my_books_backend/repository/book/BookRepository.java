package com.example.my_books_backend.repository.book;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.my_books_backend.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, String>, BookRepositoryCustom {
    // 書籍一覧取得
    Page<Book> findByIsDeletedFalse(Pageable pageable);

    // タイトル検索
    Page<Book> findByTitleContainingAndIsDeletedFalse(String keyword, Pageable pageable);

    // 指定されたジャンルIDのリストを取得（OR条件）
    Page<Book> findDistinctByGenres_IdInAndIsDeletedFalse(List<Long> genreIds, Pageable pageable);

    // 指定されたジャンルIDのリストを取得（AND条件）
    @Query("""
            SELECT b FROM Book b
            JOIN b.genres bg
            WHERE bg.id IN :genreIds
            GROUP BY b.id
            HAVING COUNT(DISTINCT bg.id) = :size
            """)
    Page<Book> findBooksHavingAllGenres(@Param("genreIds") List<Long> genreIds,
            @Param("size") Integer size, Pageable pageable);
}
