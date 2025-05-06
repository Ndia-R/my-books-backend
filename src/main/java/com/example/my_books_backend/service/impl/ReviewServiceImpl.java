package com.example.my_books_backend.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.my_books_backend.dto.review.ReviewPageResponse;
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
import com.example.my_books_backend.util.PaginationUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    private final BookRepository bookRepository;
    private final PaginationUtil paginationUtil;

    /** ユーザーが投稿したすべてのレビュー情報のデフォルトソート（作成日） */
    private static final Sort USER_REVIEWS_DEFAULT_SORT =
            Sort.by(Sort.Order.desc("createdAt"), Sort.Order.asc("id"));

    /** 書籍に対するレビュー一覧のデフォルトソート（更新日） */
    private static final Sort BOOK_REVIEWS_DEFAULT_SORT =
            Sort.by(Sort.Order.desc("updatedAt"), Sort.Order.asc("id"));

    /**
     * {@inheritDoc}
     */
    @Override
    public ReviewResponse getUserReviewForBook(String bookId, User user) {
        Review review = reviewRepository.findByBookIdAndUserAndIsDeletedFalse(bookId, user)
                .orElseThrow(() -> new NotFoundException("Review not found"));
        return reviewMapper.toReviewResponse(review);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReviewPageResponse getUserReviews(Integer page, Integer maxResults, User user) {
        Pageable pageable =
                paginationUtil.createPageable(page, maxResults, USER_REVIEWS_DEFAULT_SORT);
        Page<Review> reviews = reviewRepository.findByUserAndIsDeletedFalse(user, pageable);
        return reviewMapper.toReviewPageResponse(reviews);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReviewPageResponse getBookReviews(String bookId, Integer page, Integer maxResults) {
        Pageable pageable =
                paginationUtil.createPageable(page, maxResults, BOOK_REVIEWS_DEFAULT_SORT);
        Page<Review> reviews = reviewRepository.findByBookIdAndIsDeletedFalse(bookId, pageable);
        return reviewMapper.toReviewPageResponse(reviews);
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
