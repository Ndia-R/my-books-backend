package com.example.my_books_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.my_books_backend.entity.Bookmark;
import com.example.my_books_backend.entity.BookmarkId;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, BookmarkId> {
    Page<Bookmark> findByUserId(Long userId, Pageable pageable);

    Integer countByUserId(Long userId);
}
