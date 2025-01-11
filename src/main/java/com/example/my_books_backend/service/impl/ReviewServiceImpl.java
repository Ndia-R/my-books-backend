package com.example.my_books_backend.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.my_books_backend.dto.review.CreateReviewRequest;
import com.example.my_books_backend.dto.review.ReviewResponse;
import com.example.my_books_backend.dto.review.UpdateReviewRequest;
import com.example.my_books_backend.entity.Review;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.mapper.ReviewMapper;
import com.example.my_books_backend.repository.ReviewRepository;
import com.example.my_books_backend.service.ReviewService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public List<ReviewResponse> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return reviewMapper.toReviewResponseList(reviews);
    }

    @Override
    public ReviewResponse getReviewById(Long id) {
        Review review = findReviewById(id);
        return reviewMapper.toReviewResponse(review);
    }

    @Override
    public ReviewResponse createReview(CreateReviewRequest request) {
        Review review = reviewMapper.toReviewEntity(request);
        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toReviewResponse(savedReview);
    }

    @Override
    public void updateReview(Long id, UpdateReviewRequest request) {
        Review review = findReviewById(id);

        String comment = request.getComment();
        Double rating = request.getRating();

        if (comment != null) {
            review.setComment(comment);
        }

        if (rating != null) {
            review.setRating(rating);
        }
        reviewRepository.save(review);
    }

    @Override
    public void deleteReview(Long id) {
        Review review = findReviewById(id);
        reviewRepository.delete(review);
    }

    @Override
    public List<ReviewResponse> getReviewsByUserId(Long userId) {
        List<Review> reviews = reviewRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        return reviewMapper.toReviewResponseList(reviews);
    }

    @Override
    public List<ReviewResponse> getReviewsByBookId(String bookId) {
        List<Review> reviews = reviewRepository.findByBookIdOrderByUpdatedAtDesc(bookId);
        return reviewMapper.toReviewResponseList(reviews);
    }

    private Review findReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("見つかりませんでした。 ID: " + id));
        return review;
    }
}
