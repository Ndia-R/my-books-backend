package com.example.my_books_backend.service;

import com.example.my_books_backend.dto.review.ReviewPageResponse;
import org.springframework.data.domain.Pageable;
import com.example.my_books_backend.dto.CursorPageResponse;
import com.example.my_books_backend.dto.review.ReviewCountsResponse;
import com.example.my_books_backend.entity.User;
import com.example.my_books_backend.dto.review.ReviewRequest;
import com.example.my_books_backend.dto.review.ReviewResponse;

public interface ReviewService {
    /**
     * ユーザーが投稿したレビューを取得
     * 
     * @param user ユーザーエンティティ
     * @param pageable ページネーション情報（ページ番号、ページサイズ、ソート条件）
     * @param bookId 書籍ID、nullの場合はすべてが対象
     * @return レビューリスト
     */
    ReviewPageResponse getUserReviews(User user, Pageable pageable, String bookId);

    /**
     * ユーザーが投稿したレビューを取得（カーソルベース）
     * 
     * @param user ユーザーエンティティ
     * @param cursor カーソルID、nullの場合は先頭からlimit分のデータが返却される
     * @param limit 1ページあたりの最大結果件数、nullの場合はデフォルト値が使用される
     * @return レビューリスト
     */
    CursorPageResponse<ReviewResponse> getUserReviewsWithCursor(User user, String cursor,
            Integer limit);

    /**
     * 書籍に対するレビューを取得
     * 
     * @param bookId 書籍ID
     * @param pageable ページネーション情報（ページ番号、ページサイズ、ソート条件）
     * @return レビューリスト
     */
    ReviewPageResponse getBookReviews(String bookId, Pageable pageable);

    /**
     * 書籍に対するレビューを取得（カーソルベース）
     * 
     * @param bookId 書籍ID
     * @param cursor カーソルID、nullの場合は先頭からlimit分のデータが返却される
     * @param limit 1ページあたりの最大結果件数、nullの場合はデフォルト値が使用される
     * @return レビューリスト
     */
    CursorPageResponse<ReviewResponse> getBookReviewsWithCursor(String bookId, String cursor,
            Integer limit);

    /**
     * 書籍に対するレビュー数などを取得 （レビュー数・平均評価点）
     * 
     * @param bookId 書籍ID
     * @return レビュー数など
     */
    ReviewCountsResponse getBookReviewCounts(String bookId);

    /**
     * レビューを作成
     * 
     * @param request レビュー作成リクエスト
     * @param user ユーザーエンティティ
     * @return 作成されたレビュー情報
     */
    ReviewResponse createReview(ReviewRequest request, User user);

    /**
     * レビューを更新
     * 
     * @param id 更新するレビューのID
     * @param request レビュー更新リクエスト
     * @param user ユーザーエンティティ
     * @return 更新されたレビュー情報
     */
    ReviewResponse updateReview(Long id, ReviewRequest request, User user);

    /**
     * レビューを削除
     * 
     * @param id 削除するレビューのID
     * @param user ユーザーエンティティ
     */
    void deleteReview(Long id, User user);
}
