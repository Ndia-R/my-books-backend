package com.example.my_books_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import com.example.my_books_backend.entity.Favorite;
import com.example.my_books_backend.entity.FavoriteId;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {
    Page<Favorite> findByUserId(Long userId, Pageable pageable);

    void deleteById(@NonNull FavoriteId favoriteId);

    Integer countByUserId(Long userId);

    Integer countByBookId(String bookId);

    Integer countByUserIdAndBookId(Long userId, String bookId);
}
