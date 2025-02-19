package com.example.my_books_backend.repository;

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
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    Page<Bookmark> findByUserAndIsDeletedFalse(User user, Pageable pageable);

    List<Bookmark> findByBookIdAndUserAndIsDeletedFalse(String bookId, User user);

    Optional<Bookmark> findByUserAndBookAndChapterNumberAndPageNumberAndIsDeletedFalse(User user,
            Book book, Integer chapterNumber, Integer pageNumber);

    Integer countByUserIdAndIsDeletedFalse(Long userId);
}
