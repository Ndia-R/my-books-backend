package com.example.my_books_backend.service;

import com.example.my_books_backend.dto.review.ReviewPageResponse;
import java.util.List;
import com.example.my_books_backend.dto.review.ReviewCountsResponse;
import com.example.my_books_backend.entity.User;
import com.example.my_books_backend.dto.review.ReviewRequest;
import com.example.my_books_backend.dto.review.ReviewResponse;

public interface ReviewService {
    /**
     * ユーザーが投稿した特定の書籍のレビューを取得
     * 
     * @param bookId 書籍ID
     * @param user ユーザーエンティティ
     * @return レビュー情報
     */
    ReviewResponse getUserReviewForBook(String bookId, User user);

    /**
     * ユーザーが投稿したすべてのレビューを取得（ページング形式）
     * 
     * @param page ページ番号（0ベース）、nullの場合はデフォルト値が使用される
     * @param maxResults 1ページあたりの最大結果件数、nullの場合はデフォルト値が使用される
     * @param user ユーザーエンティティ
     * @return レビューリスト
     */
    ReviewPageResponse getUserReviews(Integer page, Integer maxResults, User user);

    /**
     * ユーザーが投稿したすべてのレビューを取得（カーソル方式で取得）
     * 
     * @param bookId 書籍ID
     * @param cursorId カーソルID（レビューID）、nullの場合は先頭からmaxResults分のデータが返却される
     * @param maxResults 1ページあたりの最大結果件数、nullの場合はデフォルト値が使用される
     * @return レビューリスト
     */
    List<ReviewResponse> getUserReviewsByCursor(Long cursorId, Integer maxResults, User user);

    /**
     * 書籍に対するレビューリストを取得（ページング形式）
     * 
     * @param bookId 書籍ID
     * @param page ページ番号（0ベース）、nullの場合はデフォルト値が使用される
     * @param maxResults 1ページあたりの最大結果件数、nullの場合はデフォルト値が使用される
     * @return レビューリスト
     */
    ReviewPageResponse getBookReviews(String bookId, Integer page, Integer maxResults);

    /**
     * 書籍に対するレビューリストを取得（カーソル方式で取得）
     * 
     * @param bookId 書籍ID
     * @param cursorId カーソルID（レビューID）、nullの場合は先頭からmaxResults分のデータが返却される
     * @param maxResults 1ページあたりの最大結果件数、nullの場合はデフォルト値が使用される
     * @return レビューリスト
     */
    List<ReviewResponse> getBookReviewsByCursor(String bookId, Long cursorId, Integer maxResults);

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
