package com.example.my_books_backend.repository.review;

import java.util.List;
import com.example.my_books_backend.entity.Review;
import com.example.my_books_backend.entity.enums.SortableField.FieldCategory;
import com.example.my_books_backend.util.CursorQueryBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Review> findReviewsByUserIdWithCursor(Long userId, Long cursor, int limit, String sortField,
            String sortDirection) {

        // ✅ 1段階の動的クエリ生成（ユーザー別）
        Query query = CursorQueryBuilder.forEntity(Review.class, entityManager)
                .fromReviews()
                .filterByUser(userId)
                .withCursor(cursor)
                .withLimit(limit)
                .orderBy(sortField, sortDirection, FieldCategory.REVIEW)
                .build();

        @SuppressWarnings("unchecked")
        List<Review> result = query.getResultList();
        return result;
    }

    @Override
    public List<Review> findReviewsByBookIdWithCursor(String bookId, Long cursor, int limit, String sortField,
            String sortDirection) {

        // ✅ 1段階の動的クエリ生成（書籍別）
        Query query = CursorQueryBuilder.forEntity(Review.class, entityManager)
                .fromReviews()
                .filterByBook(bookId)
                .withCursor(cursor)
                .withLimit(limit)
                .orderBy(sortField, sortDirection, FieldCategory.REVIEW)
                .build();

        @SuppressWarnings("unchecked")
        List<Review> result = query.getResultList();
        return result;
    }
}
