package com.example.my_books_backend.service;

import java.util.List;
import com.example.my_books_backend.dto.review.MyReviewResponse;
import com.example.my_books_backend.dto.review.PaginatedReviewResponse;
import com.example.my_books_backend.dto.review.ReviewRequest;
import com.example.my_books_backend.dto.review.ReviewResponse;

public interface ReviewService {
    PaginatedReviewResponse getReviews(String bookId, Integer page, Integer maxResults);

    List<ReviewResponse> getReviewsByBookId(String bookId);

    List<MyReviewResponse> getMyReviews();

    ReviewResponse createReview(ReviewRequest request);

    ReviewResponse updateReview(ReviewRequest request);

    void deleteReview(String bookId);
}
