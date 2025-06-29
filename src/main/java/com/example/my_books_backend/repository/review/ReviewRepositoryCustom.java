package com.example.my_books_backend.repository.review;

import java.util.List;
import com.example.my_books_backend.entity.Review;

public interface ReviewRepositoryCustom {
    // 書籍に対するレビューを取得（カーソルベース）
    List<Review> findReviewsByBookIdWithCursor(
        String bookId,
        Long cursor,
        int limit,
        String sortString
    );

    // ユーザーが投稿したレビューを取得（カーソルベース）
    List<Review> findReviewsByUserIdWithCursor(
        Long userId,
        Long cursor,
        int limit,
        String sortString
    );
}
