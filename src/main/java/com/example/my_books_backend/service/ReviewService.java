package com.example.my_books_backend.service;

import com.example.my_books_backend.dto.review.PaginatedMyReviewResponse;
import com.example.my_books_backend.dto.review.PaginatedReviewResponse;
import com.example.my_books_backend.dto.review.ReviewRatingInfoResponse;
import com.example.my_books_backend.dto.review.ReviewRequest;
import com.example.my_books_backend.dto.review.ReviewResponse;

public interface ReviewService {
    ReviewRatingInfoResponse getReviewRatingInfo(String bookId);

    PaginatedReviewResponse getReviewsById(String bookId, Integer page, Integer maxResults);

    PaginatedMyReviewResponse getMyReviews(Integer page, Integer maxResults);

    Boolean checkMyReviewExists(String bookId);

    ReviewResponse createReview(ReviewRequest request);

    ReviewResponse updateReview(ReviewRequest request);

    void deleteReview(String bookId);
}
