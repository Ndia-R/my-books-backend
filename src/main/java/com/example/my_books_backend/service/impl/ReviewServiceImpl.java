package com.example.my_books_backend.service.impl;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.my_books_backend.dto.review.ReviewPageResponse;
import com.example.my_books_backend.dto.review.ReviewSummaryResponse;
import com.example.my_books_backend.dto.review.ReviewRequest;
import com.example.my_books_backend.dto.review.ReviewResponse;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.Review;
import com.example.my_books_backend.entity.ReviewId;
import com.example.my_books_backend.entity.User;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.mapper.ReviewMapper;
import com.example.my_books_backend.repository.BookRepository;
import com.example.my_books_backend.repository.ReviewRepository;
import com.example.my_books_backend.service.ReviewService;
import com.example.my_books_backend.util.PaginationUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    private final BookRepository bookRepository;
    private final PaginationUtil paginationUtil;

    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

    @Override
    public ReviewResponse getReviewById(String bookId, Long userId) {
        ReviewId reviewId = new ReviewId(userId, bookId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found"));
        return reviewMapper.toReviewResponse(review);
    }

    @Override
    public ReviewPageResponse getReviewPage(String bookId, Integer page, Integer maxResults) {
        Pageable pageable = paginationUtil.createPageable(page, maxResults, DEFAULT_SORT);
        Page<Review> reviewPage = reviewRepository.findByBookId(bookId, pageable);
        return reviewMapper.toReviewPageResponse(reviewPage);
    }

    @Override
    public ReviewSummaryResponse getReviewSummary(String bookId) {
        List<Review> reviews = reviewRepository.findByBookId(bookId);
        Double averageRating =
                reviews.stream().mapToDouble(Review::getRating).average().orElse(0.0);

        ReviewSummaryResponse reviewSummaryResponse = new ReviewSummaryResponse();
        reviewSummaryResponse.setBookId(bookId);
        reviewSummaryResponse.setReviewCount(reviews.size());
        reviewSummaryResponse.setAverageRating(averageRating);

        return reviewSummaryResponse;
    }

    @Override
    @Transactional
    public ReviewResponse createReview(String bookId, ReviewRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found"));
        ReviewId reviewId = new ReviewId(user.getId(), book.getId());
        Review review = new Review();
        review.setId(reviewId);
        review.setUser(user);
        review.setBook(book);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toReviewResponse(savedReview);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(String bookId, ReviewRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        ReviewId reviewId = new ReviewId(user.getId(), bookId);
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("Review not found"));

        String comment = request.getComment();
        Double rating = request.getRating();

        if (comment != null) {
            review.setComment(comment);
        }

        if (rating != null) {
            review.setRating(rating);
        }
        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toReviewResponse(savedReview);
    }

    @Override
    @Transactional
    public void deleteReview(String bookId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        ReviewId reviewId = new ReviewId(user.getId(), bookId);
        reviewRepository.deleteById(reviewId);
    }
}
