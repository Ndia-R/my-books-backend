package com.example.my_books_backend.service;

import com.example.my_books_backend.dto.review.ReviewPageResponse;
import com.example.my_books_backend.dto.review.ReviewSummaryResponse;
import com.example.my_books_backend.entity.User;
import com.example.my_books_backend.dto.review.ReviewRequest;
import com.example.my_books_backend.dto.review.ReviewResponse;

public interface ReviewService {
    ReviewPageResponse getReviewPage(String bookId, Integer page, Integer maxResults);

    ReviewSummaryResponse getReviewSummary(String bookId);

    ReviewResponse getReviewByBookId(String bookId, User user);

    ReviewPageResponse getReviewPageByUser(Integer page, Integer maxResults, User user);

    ReviewResponse createReview(ReviewRequest request, User user);

    ReviewResponse updateReview(Long id, ReviewRequest request, User user);

    void deleteReview(Long id, User user);
}
