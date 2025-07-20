package com.example.my_books_backend.service.impl;

import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.repository.BookRepository;
import com.example.my_books_backend.repository.ReviewRepository;
import com.example.my_books_backend.service.BookStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookStatsServiceImpl implements BookStatsService {

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;

    // 人気度計算の定数
    private static final double MIN_REVIEWS_FOR_POPULARITY = 3.0; // 人気度計算の最低レビュー数

    /**
     * {@inheritDoc}
     */
    @Transactional
    public void updateBookStats(String bookId) {
        Object[] stats = reviewRepository.getReviewStats(bookId);
        Integer reviewCount = (Integer) stats[0];
        Double averageRating = (Double) stats[1];

        double popularity = calculatePopularity(reviewCount, averageRating);

        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new NotFoundException("Book not found"));

        book.setReviewCount(reviewCount);
        book.setAverageRating(Math.round(averageRating * 100.0) / 100.0);
        book.setPopularity(Math.round(popularity * 1000.0) / 1000.0);

        bookRepository.save(book);
    }

    /**
     * シンプルな人気度計算
     * 
     * @param reviewCount レビュー数
     * @param averageRating 平均評価
     * @return 人気度スコア
     */
    private double calculatePopularity(Integer reviewCount, Double averageRating) {
        if (reviewCount == 0) {
            return 0.0;
        }

        // レビュー数が少ない場合は、平均評価を少し下げる
        if (reviewCount < MIN_REVIEWS_FOR_POPULARITY) {
            double penalty = 1.0 - (reviewCount / MIN_REVIEWS_FOR_POPULARITY);
            return averageRating * (1.0 - penalty * 0.2); // 最大20%のペナルティ
        }

        // レビュー数が多い場合は、平均評価をそのまま使用
        return averageRating;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Async
    public CompletableFuture<Void> updateBookStatsAsync(String bookId) {
        try {
            updateBookStats(bookId);
            log.debug("書籍ID {} の統計情報を非同期で更新完了", bookId);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("書籍ID {} の統計情報非同期更新に失敗: {}", bookId, e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void updateBookStatsBatch(List<String> bookIds) {
        log.info("バッチ処理開始: {}件の書籍の統計情報を更新", bookIds.size());

        for (String bookId : bookIds) {
            try {
                updateBookStats(bookId);
            } catch (Exception e) {
                log.error("書籍ID {} の統計情報更新に失敗: {}", bookId, e.getMessage());
                // 個別の失敗はログに記録するが、バッチ処理は継続
            }
        }

        log.info("バッチ処理完了: {}件の書籍の統計情報を更新", bookIds.size());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Async
    public CompletableFuture<Void> updateBookStatsBatchAsync(List<String> bookIds) {
        try {
            updateBookStatsBatch(bookIds);
            log.info("{}件の書籍の統計情報を非同期で一括更新完了", bookIds.size());
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("書籍統計情報の非同期一括更新に失敗: {}", e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void updateAllBookStats() {
        log.info("全書籍の統計情報更新を開始");

        int pageSize = 100;
        int pageNumber = 0;
        long totalProcessed = 0;

        while (true) {
            Page<Book> bookPage = bookRepository.findByIsDeletedFalse(
                PageRequest.of(pageNumber, pageSize)
            );

            if (bookPage.isEmpty()) {
                break;
            }

            List<String> bookIds = bookPage.getContent()
                .stream()
                .map(Book::getId)
                .toList();

            updateBookStatsBatch(bookIds);

            totalProcessed += bookPage.getContent().size();
            pageNumber++;

            log.info("進捗: {}件の書籍を処理完了", totalProcessed);
        }

        log.info("全書籍の統計情報更新完了: 合計{}件", totalProcessed);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Async
    public CompletableFuture<Void> updateAllBookStatsAsync() {
        try {
            updateAllBookStats();
            log.info("全書籍の統計情報を非同期で更新完了");
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            log.error("全書籍統計情報の非同期更新に失敗: {}", e.getMessage());
            return CompletableFuture.failedFuture(e);
        }
    }
}
