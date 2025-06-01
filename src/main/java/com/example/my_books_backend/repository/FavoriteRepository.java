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
import com.example.my_books_backend.entity.Favorite;
import com.example.my_books_backend.entity.User;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
        // ユーザーが追加したお気に入りを取得
        Page<Favorite> findByUserAndIsDeletedFalse(User user, Pageable pageable);

        // ユーザーが追加したお気に入りを取得（書籍ID指定）
        Page<Favorite> findByUserAndIsDeletedFalseAndBookId(User user, Pageable pageable,
                        String bookId);

        // ユーザーが追加したお気に入りを取得（カーソルベース）
        @Query(value = """
                        SELECT * FROM favorites f
                        WHERE (:cursor IS NULL OR
                               f.updated_at < (SELECT f2.updated_at FROM favorites f2 WHERE f2.id = :cursor) OR
                               (f.updated_at = (SELECT f2.updated_at FROM favorites f2 WHERE f2.id = :cursor) AND f.id < :cursor))
                        AND f.user_id = :userId
                        AND f.is_deleted = false
                        ORDER BY f.updated_at DESC, f.id DESC
                        LIMIT :limit
                        """,
                        nativeQuery = true)
        List<Favorite> findFavoritesByUserIdWithCursor(@Param("userId") Long userId,
                        @Param("cursor") Long cursor, @Param("limit") Integer limit);

        List<Favorite> findByUserAndBookIdAndIsDeletedFalse(User user, String bookId);

        Optional<Favorite> findByUserAndBook(User user, Book book);

        List<Favorite> findByBookId(String bookId);

        Integer countByUserIdAndIsDeletedFalse(Long userId);
}
