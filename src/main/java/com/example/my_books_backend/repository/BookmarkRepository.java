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
import com.example.my_books_backend.entity.Bookmark;
import com.example.my_books_backend.entity.User;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    // ユーザーが追加したブックマークを取得
    Page<Bookmark> findByUserAndIsDeletedFalse(User user, Pageable pageable);

    // ユーザーが追加したブックマークを取得（書籍ID指定）
    Page<Bookmark> findByUserAndIsDeletedFalseAndBookId(User user, Pageable pageable,
            String bookId);

    // ユーザーが追加したブックマークを取得（カーソルベース）
    @Query(value = """
            SELECT * FROM bookmarks b
            WHERE (:cursor IS NULL OR
                   b.updated_at < (SELECT b2.updated_at FROM bookmarks b2 WHERE b2.id = :cursor) OR
                   (b.updated_at = (SELECT b2.updated_at FROM bookmarks b2 WHERE b2.id = :cursor) AND b.id > :cursor))
            AND b.user_id = :userId
            AND b.is_deleted = false
            ORDER BY b.updated_at DESC, b.id DESC
            LIMIT :limit
            """,
            nativeQuery = true)
    List<Bookmark> findBookmarksByUserIdWithCursor(@Param("userId") Long userId,
            @Param("cursor") Long cursor, @Param("limit") Integer limit);

    List<Bookmark> findByUserAndBookIdAndIsDeletedFalse(User user, String bookId);

    Optional<Bookmark> findByUserAndBookAndChapterNumberAndPageNumber(User user, Book book,
            Integer chapterNumber, Integer pageNumber);

    Integer countByUserIdAndIsDeletedFalse(Long userId);
}
