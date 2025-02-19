package com.example.my_books_backend.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.my_books_backend.dto.review.ReviewPageResponse;
import com.example.my_books_backend.dto.review.ReviewSummaryResponse;
import com.example.my_books_backend.dto.review.ReviewRequest;
import com.example.my_books_backend.dto.review.ReviewResponse;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.Review;
import com.example.my_books_backend.entity.User;
import com.example.my_books_backend.exception.ConflictException;
import com.example.my_books_backend.exception.ForbiddenException;
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
    public ReviewPageResponse getReviewPage(String bookId, Integer page, Integer maxResults) {
        Pageable pageable = paginationUtil.createPageable(page, maxResults, DEFAULT_SORT);
        Page<Review> reviewPage = reviewRepository.findByBookIdAndIsDeletedFalse(bookId, pageable);
        return reviewMapper.toReviewPageResponse(reviewPage);
    }

    @Override
    public ReviewSummaryResponse getReviewSummary(String bookId) {
        List<Review> reviews = reviewRepository.findByBookIdAndIsDeletedFalse(bookId);
        Double averageRating =
                reviews.stream().mapToDouble(Review::getRating).average().orElse(0.0);

        ReviewSummaryResponse reviewSummaryResponse = new ReviewSummaryResponse();
        reviewSummaryResponse.setBookId(bookId);
        reviewSummaryResponse.setReviewCount(reviews.size());
        reviewSummaryResponse.setAverageRating(averageRating);

        return reviewSummaryResponse;
    }

    @Override
    public ReviewResponse getReviewByBookId(String bookId, User user) {
        Review review = reviewRepository.findByBookIdAndUserAndIsDeletedFalse(bookId, user)
                .orElseThrow(() -> new NotFoundException("Review not found"));
        return reviewMapper.toReviewResponse(review);
    }

    @Override
    public ReviewPageResponse getReviewPageByUser(Integer page, Integer maxResults, User user) {
        Pageable pageable = paginationUtil.createPageable(page, maxResults, DEFAULT_SORT);
        Page<Review> reviewPage = reviewRepository.findByUserAndIsDeletedFalse(user, pageable);
        return reviewMapper.toReviewPageResponse(reviewPage);
    }

    @Override
    @Transactional
    public ReviewResponse createReview(ReviewRequest request, User user) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new NotFoundException("Book not found"));

        Optional<Review> existingReview =
                reviewRepository.findByUserAndBookAndIsDeletedFalse(user, book);

        if (existingReview.isPresent()) {
            throw new ConflictException("すでにこの書籍にはレビューが登録されています。");
        }

        Review review = new Review();
        review.setUser(user);
        review.setBook(book);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toReviewResponse(savedReview);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(Long id, ReviewRequest request, User user) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Review not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("このレビューを編集する権限がありません。");
        }

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
    public void deleteReview(Long id, User user) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Review not found"));

        if (!review.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("このレビューを削除する権限がありません");
        }

        review.setIsDeleted(true);
        reviewRepository.save(review);
    }
}
