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
    // 書籍一覧取得
    Page<Book> findByIsDeletedFalse(Pageable pageable);

    // 書籍一覧取得（カーソルベース）
    @Query(value = """
            SELECT * FROM books b
            WHERE (:cursor IS NULL OR
                   b.publication_date < (SELECT b2.publication_date FROM books b2 WHERE b2.id = :cursor) OR
                   (b.publication_date = (SELECT b2.publication_date FROM books b2 WHERE b2.id = :cursor) AND b.id < :cursor))
            AND b.is_deleted = false
            ORDER BY b.publication_date DESC, b.id DESC
            LIMIT :limit
            """,
            nativeQuery = true)
    List<Book> findBooksWithCursor(@Param("cursor") String cursor, @Param("limit") int limit);

    // タイトル検索
    Page<Book> findByTitleContainingAndIsDeletedFalse(String keyword, Pageable pageable);

    // タイトル検索（カーソルベース）
    @Query(value = """
            SELECT * FROM books b
            WHERE (:cursor IS NULL OR
                   b.publication_date < (SELECT b2.publication_date FROM books b2 WHERE b2.id = :cursor) OR
                   (b.publication_date = (SELECT b2.publication_date FROM books b2 WHERE b2.id = :cursor) AND b.id < :cursor))
            AND b.title LIKE :keyword
            AND b.is_deleted = false
            ORDER BY b.publication_date DESC, b.id DESC
            LIMIT :limit
            """,
            nativeQuery = true)
    List<Book> findBooksByTitleKeywordWithCursor(@Param("keyword") String keyword,
            @Param("cursor") String cursor, @Param("limit") int limit);

    // 指定されたジャンルIDのリストを取得（OR条件）
    Page<Book> findDistinctByGenres_IdIn(List<Long> genreIds, Pageable pageable);

    // 指定されたジャンルIDのリストを取得（AND条件）
    @Query("""
            SELECT b FROM Book b
            JOIN b.genres bg
            WHERE bg.id IN :genreIds
            GROUP BY b.id
            HAVING COUNT(DISTINCT bg.id) = :size
            """)
    Page<Book> findDistinctByGenres_IdInAndIsDeletedFalse(@Param("genreIds") List<Long> genreIds,
            @Param("size") long size, Pageable pageable);
}
