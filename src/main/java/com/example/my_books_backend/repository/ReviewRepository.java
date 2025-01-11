package com.example.my_books_backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.my_books_backend.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUserIdOrderByUpdatedAtDesc(Long userId);

    List<Review> findByBookIdOrderByUpdatedAtDesc(String bookId);
}
