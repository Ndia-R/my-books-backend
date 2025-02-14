package com.example.my_books_backend.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.my_books_backend.entity.Favorite;
import com.example.my_books_backend.entity.FavoriteId;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {
    List<Favorite> findByBookId(String bookId);

    Page<Favorite> findByUserId(Long userId, Pageable pageable);

    Integer countByUserId(Long userId);
}
