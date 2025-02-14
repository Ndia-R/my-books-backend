package com.example.my_books_backend.service;

import com.example.my_books_backend.dto.review.ReviewPageResponse;
import com.example.my_books_backend.dto.review.ReviewSummaryResponse;
import com.example.my_books_backend.dto.review.ReviewRequest;
import com.example.my_books_backend.dto.review.ReviewResponse;

public interface ReviewService {
    ReviewResponse getReviewById(String bookId, Long userId);

    ReviewPageResponse getReviewPage(String bookId, Integer page, Integer maxResults);

    ReviewSummaryResponse getReviewSummary(String bookId);

    ReviewResponse createReview(String bookId, ReviewRequest request);

    ReviewResponse updateReview(String bookId, ReviewRequest request);

    void deleteReview(String bookId);
}
