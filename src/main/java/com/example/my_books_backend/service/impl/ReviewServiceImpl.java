package com.example.my_books_backend.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.my_books_backend.dto.review.PaginatedMyReviewResponse;
import com.example.my_books_backend.dto.review.PaginatedReviewResponse;
import com.example.my_books_backend.dto.review.ReviewRatingInfoResponse;
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
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;
    private final BookRepository bookRepository;

    private static final Integer DEFAULT_START_PAGE = 0;
    private static final Integer DEFAULT_MAX_RESULTS = 5;
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

    @Override
    public ReviewRatingInfoResponse getReviewRatingInfo(String bookId) {
        Double rating = reviewRepository.findAverageRatingByBookId(bookId);
        Integer reviewCount = reviewRepository.countByBookId(bookId);

        if (rating == null) {
            rating = 0.0;
        }

        ReviewRatingInfoResponse reviewRatingInfoResponse = new ReviewRatingInfoResponse();
        reviewRatingInfoResponse.setBookId(bookId);
        reviewRatingInfoResponse.setRating(rating);
        reviewRatingInfoResponse.setReviewCount(reviewCount);

        return reviewRatingInfoResponse;
    }

    @Override
    public PaginatedReviewResponse getReviewsById(String bookId, Integer page, Integer maxResults) {
        Pageable pageable = createPageable(page, maxResults);
        Page<Review> reviews = reviewRepository.findByBookId(bookId, pageable);
        return reviewMapper.toPaginatedReviewResponse(reviews);
    }

    @Override
    public PaginatedMyReviewResponse getMyReviews(Integer page, Integer maxResults) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Pageable pageable = createPageable(page, maxResults);
        Page<Review> reviews = reviewRepository.findByUserId(user.getId(), pageable);
        return reviewMapper.toPaginatedMyReviewResponse(reviews);
    }

    @Override
    public Boolean checkMyReviewExists(String bookId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return reviewRepository.existsByUserIdAndBookId(user.getId(), bookId);
    }

    @Override
    @Transactional
    public ReviewResponse createReview(ReviewRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Book book = bookRepository.findById(request.getBookId())
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
    public ReviewResponse updateReview(ReviewRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        ReviewId reviewId = new ReviewId(user.getId(), request.getBookId());
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

    private Pageable createPageable(Integer page, Integer maxResults) {
        page = (page != null) ? page : DEFAULT_START_PAGE;
        maxResults = (maxResults != null) ? maxResults : DEFAULT_MAX_RESULTS;
        return PageRequest.of(page, maxResults, DEFAULT_SORT);
    }
}
