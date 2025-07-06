package com.example.my_books_backend.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.my_books_backend.dto.PageResponse;
import com.example.my_books_backend.dto.SliceResponse;
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
import com.example.my_books_backend.service.BookStatsService;
import com.example.my_books_backend.service.ReviewService;
import com.example.my_books_backend.util.PageableUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    private final BookRepository bookRepository;
    private final BookStatsService bookRatingService;

    /**
     * {@inheritDoc}
     */
    @Override
    public PageResponse<ReviewResponse> getUserReviews(
        User user,
        Integer page,
        Integer size,
        String sortString,
        String bookId
    ) {
        Pageable pageable = PageableUtils.createPageable(
            page,
            size,
            sortString,
            PageableUtils.REVIEW_ALLOWED_FIELDS
        );
        Page<Review> reviews = (bookId == null)
            ? reviewRepository.findByUserAndIsDeletedFalse(user, pageable)
            : reviewRepository.findByUserAndIsDeletedFalseAndBookId(user, pageable, bookId);
        return reviewMapper.toPageResponse(reviews);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public SliceResponse<ReviewResponse> getUserReviewsForScroll(
        User user,
        Integer page,
        Integer size,
        String sortString,
        String bookId
    ) {
        Pageable pageable = PageableUtils.createPageable(
            page,
            size,
            sortString,
            PageableUtils.REVIEW_ALLOWED_FIELDS
        );
        Slice<Review> reviews = (bookId == null)
            ? reviewRepository.findSliceByUserAndIsDeletedFalse(user, pageable)
            : reviewRepository.findSliceByUserAndIsDeletedFalseAndBookId(user, pageable, bookId);
        return reviewMapper.toSliceResponse(reviews);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageResponse<ReviewResponse> getBookReviews(
        String bookId,
        Integer page,
        Integer size,
        String sortString
    ) {
        Pageable pageable = PageableUtils.createPageable(
            page,
            size,
            sortString,
            PageableUtils.REVIEW_ALLOWED_FIELDS
        );
        Page<Review> reviews = reviewRepository.findByBookIdAndIsDeletedFalse(bookId, pageable);
        return reviewMapper.toPageResponse(reviews);
    }

    /**
    * {@inheritDoc}
    */
    @Override
    public SliceResponse<ReviewResponse> getBookReviewsForScroll(
        String bookId,
        Integer page,
        Integer size,
        String sortString
    ) {
        Pageable pageable = PageableUtils.createPageable(
            page,
            size,
            sortString,
            PageableUtils.REVIEW_ALLOWED_FIELDS
        );
        Slice<Review> reviews = reviewRepository.findSliceByBookIdAndIsDeletedFalse(bookId, pageable);
        return reviewMapper.toSliceResponse(reviews);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReviewCountsResponse getBookReviewCounts(String bookId) {
        List<Review> reviews = reviewRepository.findByBookIdAndIsDeletedFalse(bookId);
        Double averageRating = reviews.stream().mapToDouble(Review::getRating).average().orElse(0.0);

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

        // 書籍の評価点を更新
        bookRatingService.updateBookStats(savedReview.getBook().getId());

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

        // 書籍の評価点を更新
        bookRatingService.updateBookStats(savedReview.getBook().getId());

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

        // 書籍の評価点を更新
        bookRatingService.updateBookStats(review.getBook().getId());
    }
}
