package com.example.my_books_backend.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.my_books_backend.entity.Review;
import com.example.my_books_backend.entity.ReviewId;

@Repository
public interface ReviewRepository extends JpaRepository<Review, ReviewId> {
    List<Review> findByBookId(String bookId);

    Page<Review> findByBookId(String bookId, Pageable pageable);

    Integer countByUserId(Long userId);
}
