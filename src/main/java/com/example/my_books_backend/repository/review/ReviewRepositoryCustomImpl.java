package com.example.my_books_backend.repository.review;

import java.util.List;
import com.example.my_books_backend.entity.Review;
import com.example.my_books_backend.util.CursorQueryBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Review> findReviewsByUserIdWithCursor(
        Long userId,
        Long cursor,
        int limit,
        String sortString
    ) {

        Query query = CursorQueryBuilder
            .of(Review.class, entityManager)
            .filterByUser(userId) // ユーザー別
            .withCursor(cursor)
            .withLimit(limit)
            .orderBy(sortString)
            .build();

        @SuppressWarnings("unchecked")
        List<Review> result = query.getResultList();
        return result;
    }

    @Override
    public List<Review> findReviewsByBookIdWithCursor(
        String bookId,
        Long cursor,
        int limit,
        String sortString
    ) {

        Query query = CursorQueryBuilder
            .of(Review.class, entityManager)
            .filterByBook(bookId) // 書籍別
            .withCursor(cursor)
            .withLimit(limit)
            .orderBy(sortString)
            .build();

        @SuppressWarnings("unchecked")
        List<Review> result = query.getResultList();
        return result;
    }
}
