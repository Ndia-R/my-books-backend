package com.example.my_books_backend.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.my_books_backend.entity.Favorite;
import com.example.my_books_backend.entity.FavoriteId;
import com.example.my_books_backend.entity.User;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {
    Page<Favorite> findByUser(User user, Pageable pageable);

    // カーソル方式のページネーション（cursorIdをカーソルとして使用し、updated_atを基準に降順に並べ替える）
    @Query("""
            SELECT r FROM Favorite r
            WHERE (:cursorId IS NULL OR
                   r.updatedAt < (SELECT r2.updatedAt FROM Favorite r2 WHERE r2.id = :cursorId) OR
                   (r.updatedAt = (SELECT r2.updatedAt FROM Favorite r2 WHERE r2.id = :cursorId) AND r.id > :cursorId))
            AND r.user.id = :userId AND r.isDeleted = false
            ORDER BY r.updatedAt DESC, r.id ASC
            """)
    List<Favorite> findFavoritesByUserIdWithCursor(@Param("userId") Long userId,
            @Param("cursorId") Long cursorId, Pageable pageable);

    List<Favorite> findByBookId(String bookId);

    Integer countByUserId(Long userId);
}
