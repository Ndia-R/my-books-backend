package com.example.my_books_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import com.example.my_books_backend.entity.FavoriteId;
import com.example.my_books_backend.entity.Review;
import com.example.my_books_backend.entity.ReviewId;

@Repository
public interface ReviewRepository extends JpaRepository<Review, ReviewId> {
    Page<Review> findByBookId(String bookId, Pageable pageable);

    Page<Review> findByUserId(Long userId, Pageable pageable);

    void deleteById(@NonNull FavoriteId favoriteId);

    Integer countByUserId(Long userId);

    Integer countByBookId(String bookId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.book.id = :bookId")
    Double findAverageRatingByBookId(@Param("bookId") String bookId);

    Boolean existsByUserIdAndBookId(Long userId, String bookId);
}
