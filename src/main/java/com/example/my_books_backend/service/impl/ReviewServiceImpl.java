package com.example.my_books_backend.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.my_books_backend.dto.review.ReviewPageResponse;
import com.example.my_books_backend.dto.CursorPageResponse;
import com.example.my_books_backend.dto.review.ReviewCountsResponse;
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
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    private final BookRepository bookRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public ReviewPageResponse getUserReviews(User user, Pageable pageable, String bookId) {
        Page<Review> reviews = (bookId == null)
                ? reviewRepository.findByUserAndIsDeletedFalse(user, pageable)
                : reviewRepository.findByUserAndIsDeletedFalseAndBookId(user, pageable, bookId);
        return reviewMapper.toReviewPageResponse(reviews);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CursorPageResponse<ReviewResponse> getUserReviewsWithCursor(User user, String cursor,
            Integer limit) {
        // 次のページの有無を判定するために、1件多く取得
        List<Review> reviews = reviewRepository.findReviewsByUserIdWithCursor(user.getId(),
                (cursor != null) ? Long.parseLong(cursor) : null, limit + 1);
        return reviewMapper.toCursorPageResponse(reviews, limit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReviewPageResponse getBookReviews(String bookId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByBookIdAndIsDeletedFalse(bookId, pageable);
        return reviewMapper.toReviewPageResponse(reviews);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CursorPageResponse<ReviewResponse> getBookReviewsWithCursor(String bookId, String cursor,
            Integer limit) {
        // 次のページの有無を判定するために、1件多く取得
        List<Review> reviews = reviewRepository.findReviewsByBookIdWithCursor(bookId,
                (cursor != null) ? Long.parseLong(cursor) : null, limit + 1);
        return reviewMapper.toCursorPageResponse(reviews, limit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReviewCountsResponse getBookReviewCounts(String bookId) {
        List<Review> reviews = reviewRepository.findByBookIdAndIsDeletedFalse(bookId);
        Double averageRating =
                reviews.stream().mapToDouble(Review::getRating).average().orElse(0.0);

        ReviewCountsResponse response = new ReviewCountsResponse();
        response.setBookId(bookId);
        response.setReviewCount(reviews.size());
        response.setAverageRating(averageRating);

        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public ReviewResponse createReview(ReviewRequest request, User user) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new NotFoundException("Book not found"));

        Optional<Review> existingReview = reviewRepository.findByUserAndBook(user, book);

        Review review = new Review();
        if (existingReview.isPresent()) {
            review = existingReview.get();
            if (review.getIsDeleted()) {
                review.setIsDeleted(false);
            } else {
                throw new ConflictException("すでにこの書籍にはレビューが登録されています。");
            }
        }
        review.setUser(user);
        review.setBook(book);
        review.setRating(request.getRating());
        review.setComment(request.getComment());

        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toReviewResponse(savedReview);
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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
