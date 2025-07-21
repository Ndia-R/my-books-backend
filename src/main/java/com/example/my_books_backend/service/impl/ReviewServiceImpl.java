package com.example.my_books_backend.service.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.my_books_backend.dto.PageResponse;
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
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    private final BookRepository bookRepository;
    private final BookStatsService bookStatsService;

    /**
     * {@inheritDoc}
     */
    @Override
    public PageResponse<ReviewResponse> getUserReviews(
        User user,
        Long page,
        Long size,
        String sortString,
        String bookId
    ) {
        Pageable pageable = PageableUtils.createPageable(
            page,
            size,
            sortString,
            PageableUtils.REVIEW_ALLOWED_FIELDS
        );
        Page<Review> pageObj = (bookId == null)
            ? reviewRepository.findByUserAndIsDeletedFalse(user, pageable)
            : reviewRepository.findByUserAndIsDeletedFalseAndBookId(user, pageable, bookId);

        // 2クエリ戦略：IDリストから関連データを含むリストを取得
        List<Long> ids = pageObj.getContent().stream().map(Review::getId).toList();
        List<Review> list = reviewRepository.findAllByIdInWithRelations(ids);

        // ソート順序を復元
        List<Review> sortedList = PageableUtils.restoreSortOrder(ids, list, Review::getId);

        // 元のページネーション情報を保持して新しいPageオブジェクトを作成
        Page<Review> updatedPageObj = new PageImpl<>(
            sortedList,
            pageable,
            pageObj.getTotalElements()
        );

        return reviewMapper.toPageResponse(updatedPageObj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageResponse<ReviewResponse> getBookReviews(
        String bookId,
        Long page,
        Long size,
        String sortString
    ) {
        Pageable pageable = PageableUtils.createPageable(
            page,
            size,
            sortString,
            PageableUtils.REVIEW_ALLOWED_FIELDS
        );
        Page<Review> pageObj = reviewRepository.findByBookIdAndIsDeletedFalse(bookId, pageable);

        // 2クエリ戦略：IDリストから関連データを含むリストを取得
        List<Long> ids = pageObj.getContent().stream().map(Review::getId).toList();
        List<Review> list = reviewRepository.findAllByIdInWithRelations(ids);

        // ソート順序を復元
        List<Review> sortedList = PageableUtils.restoreSortOrder(ids, list, Review::getId);

        // 元のページネーション情報を保持して新しいPageオブジェクトを作成
        Page<Review> updatedPageObj = new PageImpl<>(
            sortedList,
            pageable,
            pageObj.getTotalElements()
        );

        return reviewMapper.toPageResponse(updatedPageObj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReviewCountsResponse getBookReviewCounts(String bookId) {
        Object[] stats = reviewRepository.getReviewStats(bookId);
        Long reviewCount = (Long) stats[0];
        Double averageRating = (Double) stats[1];

        ReviewCountsResponse response = new ReviewCountsResponse();
        response.setBookId(bookId);
        response.setReviewCount(reviewCount);
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

        // 書籍の評価点を非同期で更新
        bookStatsService.updateBookStatsAsync(savedReview.getBook().getId());

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

        // 書籍の評価点を非同期で更新
        bookStatsService.updateBookStatsAsync(savedReview.getBook().getId());

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

        // 書籍の評価点を非同期で更新
        bookStatsService.updateBookStatsAsync(review.getBook().getId());
    }
}
