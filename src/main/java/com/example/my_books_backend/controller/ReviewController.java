package com.example.my_books_backend.controller;

import java.net.URI;
import java.util.List;
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
import com.example.my_books_backend.dto.review.MyReviewResponse;
import com.example.my_books_backend.dto.review.PaginatedReviewResponse;
import com.example.my_books_backend.dto.review.ReviewRequest;
import com.example.my_books_backend.dto.review.ReviewResponse;
import com.example.my_books_backend.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/{bookId}")
    public ResponseEntity<PaginatedReviewResponse> getReviews(@PathVariable String bookId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer maxResults) {
        PaginatedReviewResponse reviews = reviewService.getReviews(bookId, page, maxResults);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByBookId(@PathVariable String bookId) {
        List<ReviewResponse> reviews = reviewService.getReviewsByBookId(bookId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("my-reviews")
    public ResponseEntity<List<MyReviewResponse>> getMyReviews() {
        List<MyReviewResponse> myReviews = reviewService.getMyReviews();
        return ResponseEntity.ok(myReviews);
    }

    @PostMapping("")
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest request) {
        ReviewResponse review = reviewService.createReview(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(review.getReviewId()).toUri();
        return ResponseEntity.created(location).body(review);
    }

    @PutMapping("")
    public ResponseEntity<ReviewResponse> updateReview(@Valid @RequestBody ReviewRequest request) {
        ReviewResponse review = reviewService.updateReview(request);
        return ResponseEntity.ok(review);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteReview(@PathVariable String bookId) {
        reviewService.deleteReview(bookId);
        return ResponseEntity.noContent().build();
    }
}
