package com.example.my_books_backend.controller;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.example.my_books_backend.dto.review.ReviewPageResponse;
import com.example.my_books_backend.dto.review.ReviewSummaryResponse;
import com.example.my_books_backend.dto.review.ReviewRequest;
import com.example.my_books_backend.dto.review.ReviewResponse;
import com.example.my_books_backend.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/books/{bookId}/reviews")
    public ResponseEntity<ReviewPageResponse> getReviews(@PathVariable String bookId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer maxResults) {
        ReviewPageResponse reviewPageResponse = reviewService.getReviews(bookId, page, maxResults);
        return ResponseEntity.ok(reviewPageResponse);
    }

    @GetMapping("/books/{bookId}/reviews/{userId}")
    public ResponseEntity<ReviewResponse> getReviewByUserId(@PathVariable String bookId,
            @PathVariable Long userId) {
        ReviewResponse reviewResponse = reviewService.getReviewByUserId(bookId, userId);
        return ResponseEntity.ok(reviewResponse);
    }

    @GetMapping("/books/{bookId}/reviews/summary")
    public ResponseEntity<ReviewSummaryResponse> getReviewSummary(@PathVariable String bookId) {
        ReviewSummaryResponse reviewSummaryResponse = reviewService.getReviewSummary(bookId);
        return ResponseEntity.ok(reviewSummaryResponse);
    }

    @PostMapping("/books/{bookId}/reviews")
    public ResponseEntity<ReviewResponse> createReview(@PathVariable String bookId,
            @Valid @RequestBody ReviewRequest request) {
        ReviewResponse reviewResponse = reviewService.createReview(bookId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{userId}")
                .buildAndExpand(reviewResponse.getUserId()).toUri();
        return ResponseEntity.created(location).body(reviewResponse);
    }

    @PutMapping("/books/{bookId}/reviews")
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable String bookId,
            @Valid @RequestBody ReviewRequest request) {
        ReviewResponse reviewResponse = reviewService.updateReview(bookId, request);
        return ResponseEntity.ok(reviewResponse);
    }

    @DeleteMapping("/books/{bookId}/reviews")
    public ResponseEntity<Void> deleteReview(@PathVariable String bookId) {
        reviewService.deleteReview(bookId);
        return ResponseEntity.noContent().build();
    }
}
