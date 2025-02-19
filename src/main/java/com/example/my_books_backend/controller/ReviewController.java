package com.example.my_books_backend.controller;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import com.example.my_books_backend.entity.User;
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
    public ResponseEntity<ReviewPageResponse> getReviewPage(@PathVariable String bookId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer maxResults) {
        ReviewPageResponse reviewPageResponse =
                reviewService.getReviewPage(bookId, page, maxResults);
        return ResponseEntity.ok(reviewPageResponse);
    }

    @GetMapping("/books/{bookId}/reviews/summary")
    public ResponseEntity<ReviewSummaryResponse> getReviewSummary(@PathVariable String bookId) {
        ReviewSummaryResponse reviewSummaryResponse = reviewService.getReviewSummary(bookId);
        return ResponseEntity.ok(reviewSummaryResponse);
    }

    @GetMapping("/reviews/{bookId}")
    public ResponseEntity<ReviewResponse> getReviewByBookId(@PathVariable String bookId,
            @AuthenticationPrincipal User user) {
        ReviewResponse reviewResponse = reviewService.getReviewByBookId(bookId, user);
        return ResponseEntity.ok(reviewResponse);
    }

    @GetMapping("/reviews")
    public ResponseEntity<ReviewPageResponse> getReviewPageByUser(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer maxResults,
            @AuthenticationPrincipal User user) {
        ReviewPageResponse reviewPageResponse =
                reviewService.getReviewPageByUser(page, maxResults, user);
        return ResponseEntity.ok(reviewPageResponse);
    }

    @PostMapping("/reviews")
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal User user) {
        ReviewResponse reviewResponse = reviewService.createReview(request, user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{bookId}")
                .buildAndExpand(reviewResponse.getBookId()).toUri();
        return ResponseEntity.created(location).body(reviewResponse);
    }

    @PutMapping("/reviews/{id}")
    public ResponseEntity<ReviewResponse> updateReview(@PathVariable Long id,
            @Valid @RequestBody ReviewRequest request, @AuthenticationPrincipal User user) {
        ReviewResponse reviewResponse = reviewService.updateReview(id, request, user);
        return ResponseEntity.ok(reviewResponse);
    }

    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id,
            @AuthenticationPrincipal User user) {
        reviewService.deleteReview(id, user);
        return ResponseEntity.noContent().build();
    }
}
