package com.example.my_books_backend.repository.bookmark;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.Bookmark;
import com.example.my_books_backend.entity.User;

@Repository
public interface BookmarkRepository
        extends JpaRepository<Bookmark, Long>, BookmarkRepositoryCustom {
    // ユーザーが追加したブックマークを取得
    Page<Bookmark> findByUserAndIsDeletedFalse(User user, Pageable pageable);

    // ユーザーが追加したブックマークを取得（書籍ID指定）
    Page<Bookmark> findByUserAndIsDeletedFalseAndBookId(User user, Pageable pageable,
            String bookId);

    List<Bookmark> findByUserAndBookIdAndIsDeletedFalse(User user, String bookId);

    Optional<Bookmark> findByUserAndBookAndChapterNumberAndPageNumber(User user, Book book,
            Integer chapterNumber, Integer pageNumber);

    Integer countByUserIdAndIsDeletedFalse(Long userId);
}
