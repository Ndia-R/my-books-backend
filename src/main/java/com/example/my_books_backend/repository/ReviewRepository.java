package com.example.my_books_backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.Review;
import com.example.my_books_backend.entity.User;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByBookIdAndIsDeletedFalse(String bookId, Pageable pageable);

    Optional<Review> findByBookIdAndUserAndIsDeletedFalse(String bookId, User user);

    Page<Review> findByUserAndIsDeletedFalse(User user, Pageable pageable);

    List<Review> findByBookIdAndIsDeletedFalse(String bookId);

    Optional<Review> findByUserAndBook(User user, Book book);

    Integer countByUserIdAndIsDeletedFalse(Long userId);
}
