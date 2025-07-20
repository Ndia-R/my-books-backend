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
    Page<Bookmark> findByUserAndIsDeletedFalseAndBookId(User user, Pageable pageable, String bookId);

    // ユーザーが追加したブックマークを取得（書籍、章番号、ページ番号指定）
    Optional<Bookmark> findByUserAndBookAndChapterNumberAndPageNumber(
        User user,
        Book book,
        Integer chapterNumber,
        Integer pageNumber
    );

    // ユーザーが追加したブックマーク数を取得
    Integer countByUserIdAndIsDeletedFalse(Long userId);

    // 2クエリ戦略用：IDリストから関連データを含むリストを取得
    @Query("""
        SELECT DISTINCT b
        FROM Bookmark b
        LEFT JOIN FETCH b.user
        LEFT JOIN FETCH b.book b2
        LEFT JOIN FETCH b2.genres
        WHERE b.id IN :ids
        """)
    List<Bookmark> findAllByIdInWithRelations(@Param("ids") List<Long> ids);
}
