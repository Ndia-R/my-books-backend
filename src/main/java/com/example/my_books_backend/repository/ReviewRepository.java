package com.example.my_books_backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.Review;
import com.example.my_books_backend.entity.User;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    // ユーザーが投稿したレビューを取得
    Page<Review> findByUserAndIsDeletedFalse(User user, Pageable pageable);

    // ユーザーが投稿したレビューを取得（書籍ID指定）
    Page<Review> findByUserAndIsDeletedFalseAndBookId(User user, Pageable pageable, String bookId);

    // ユーザーが投稿したレビューを取得（カーソルベース）
    @Query(value = """
            SELECT * FROM reviews r
            WHERE (:cursor IS NULL OR
                   r.updated_at < (SELECT r2.updated_at FROM reviews r2 WHERE r2.id = :cursor) OR
                   (r.updated_at = (SELECT r2.updated_at FROM reviews r2 WHERE r2.id = :cursor) AND r.id > :cursor))
            AND r.user_id = :userId
            AND r.is_deleted = false
            ORDER BY r.updated_at DESC, r.id DESC
            LIMIT :limit
            """,
            nativeQuery = true)
    List<Review> findReviewsByUserIdWithCursor(@Param("userId") Long userId,
            @Param("cursor") Long cursor, @Param("limit") Integer limit);

    // 書籍に対するレビューを取得
    Page<Review> findByBookIdAndIsDeletedFalse(String bookId, Pageable pageable);

    // 書籍に対するレビューを取得（カーソルベース）
    @Query(value = """
            SELECT * FROM reviews r
            WHERE (:cursor IS NULL OR
                   r.updated_at < (SELECT r2.updated_at FROM reviews r2 WHERE r2.id = :cursor) OR
                   (r.updated_at = (SELECT r2.updated_at FROM reviews r2 WHERE r2.id = :cursor) AND r.id > :cursor))
            AND r.book_id = :bookId
            AND r.is_deleted = false
            ORDER BY r.updated_at DESC, r.id DESC
            LIMIT :limit
            """,
            nativeQuery = true)
    List<Review> findReviewsByBookIdWithCursor(@Param("bookId") String bookId,
            @Param("cursor") Long cursor, @Param("limit") Integer limit);

    List<Review> findByBookIdAndIsDeletedFalse(String bookId);

    Optional<Review> findByUserAndBook(User user, Book book);

    Integer countByUserIdAndIsDeletedFalse(Long userId);
}
