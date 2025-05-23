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
    Optional<Review> findByBookIdAndUserAndIsDeletedFalse(String bookId, User user);

    Page<Review> findByUserAndIsDeletedFalse(User user, Pageable pageable);

    // カーソル方式のページネーション（cursorIdをカーソルとして使用し、updated_atを基準に降順に並べ替える）
    @Query("""
            SELECT r FROM Review r
            WHERE (:cursorId IS NULL OR
                   r.updatedAt < (SELECT r2.updatedAt FROM Review r2 WHERE r2.id = :cursorId) OR
                   (r.updatedAt = (SELECT r2.updatedAt FROM Review r2 WHERE r2.id = :cursorId) AND r.id > :cursorId))
            AND r.user.id = :userId AND r.isDeleted = false
            ORDER BY r.updatedAt DESC, r.id ASC
            """)
    List<Review> findReviewsByUserIdWithCursor(@Param("userId") Long userId,
            @Param("cursorId") Long cursorId, Pageable pageable);

    Page<Review> findByBookIdAndIsDeletedFalse(String bookId, Pageable pageable);

    // カーソル方式のページネーション（cursorIdをカーソルとして使用し、updated_atを基準に降順に並べ替える）
    @Query("""
            SELECT r FROM Review r
            WHERE (:cursorId IS NULL OR
                   r.updatedAt < (SELECT r2.updatedAt FROM Review r2 WHERE r2.id = :cursorId) OR
                   (r.updatedAt = (SELECT r2.updatedAt FROM Review r2 WHERE r2.id = :cursorId) AND r.id > :cursorId))
            AND r.book.id = :bookId AND r.isDeleted = false
            ORDER BY r.updatedAt DESC, r.id ASC
            """)
    List<Review> findReviewsByBookIdWithCursor(@Param("bookId") String bookId,
            @Param("cursorId") Long cursorId, Pageable pageable);

    List<Review> findByBookIdAndIsDeletedFalse(String bookId);

    Optional<Review> findByUserAndBook(User user, Book book);

    Integer countByUserIdAndIsDeletedFalse(Long userId);
}
