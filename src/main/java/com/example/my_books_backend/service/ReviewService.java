package com.example.my_books_backend.service;

import java.util.List;
import com.example.my_books_backend.dto.review.CreateReviewRequest;
import com.example.my_books_backend.dto.review.ReviewResponse;
import com.example.my_books_backend.dto.review.UpdateReviewRequest;

public interface ReviewService {
    List<ReviewResponse> getAllReviews();

    ReviewResponse getReviewById(Long id);

    ReviewResponse createReview(CreateReviewRequest request);

    void updateReview(Long id, UpdateReviewRequest request);

    void deleteReview(Long id);

    List<ReviewResponse> getReviewsByUserId(Long userId);

    List<ReviewResponse> getReviewsByBookId(String bookId);
}
