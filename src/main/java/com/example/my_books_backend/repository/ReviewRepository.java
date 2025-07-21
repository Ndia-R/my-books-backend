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

    // 書籍に対するレビューを取得
    Page<Review> findByBookIdAndIsDeletedFalse(String bookId, Pageable pageable);

    List<Review> findByBookIdAndIsDeletedFalse(String bookId);

    Optional<Review> findByUserAndBook(User user, Book book);

    // 2クエリ戦略用：IDリストから関連データを含むリストを取得
    @Query("""
        SELECT DISTINCT r
        FROM Review r
        LEFT JOIN FETCH r.user
        LEFT JOIN FETCH r.book b
        LEFT JOIN FETCH b.genres
        WHERE r.id IN :ids
        """)
    List<Review> findAllByIdInWithRelations(@Param("ids") List<Long> ids);

    // 書籍に対するレビュー数と平均評価を取得
    @Query("""
        SELECT COUNT(r), COALESCE(AVG(r.rating), 0.0)
        FROM Review r
        WHERE r.book.id = :bookId AND r.isDeleted = false
        """)
    Object[] getReviewStats(@Param("bookId") String bookId);
}
