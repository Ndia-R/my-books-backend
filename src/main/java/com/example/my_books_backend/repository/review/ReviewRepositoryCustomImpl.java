package com.example.my_books_backend.repository.review;

import java.util.List;
import com.example.my_books_backend.entity.Review;
import com.example.my_books_backend.util.StringCaseUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

public class ReviewRepositoryCustomImpl implements ReviewRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Review> findReviewsByUserIdWithCursor(Long userId, Long cursor, int limit,
            String sortField, String sortDirection) {

        String columnName = StringCaseUtils.camelToSnake(sortField);
        String comparison = "asc".equalsIgnoreCase(sortDirection) ? ">" : "<";
        String orderDirection = "asc".equalsIgnoreCase(sortDirection) ? "ASC" : "DESC";

        String sql = String.format(
                """
                        SELECT * FROM reviews r
                        WHERE (:cursor IS NULL OR
                            (r.%s %s (SELECT r2.%s FROM reviews r2 WHERE r2.id = :cursor) OR
                            (r.%s = (SELECT r2.%s FROM reviews r2 WHERE r2.id = :cursor) AND r.id > :cursor)))
                        AND r.user_id = :userId
                        AND r.is_deleted = false
                        ORDER BY
                            r.%s %s,
                            r.id ASC
                        LIMIT :limit
                        """,
                columnName, comparison, columnName, columnName, columnName, columnName,
                orderDirection);

        Query query = entityManager.createNativeQuery(sql, Review.class);
        query.setParameter("userId", userId);
        query.setParameter("cursor", cursor);
        query.setParameter("limit", limit);

        @SuppressWarnings("unchecked")
        List<Review> result = query.getResultList();
        return result;
    }

    @Override
    public List<Review> findReviewsByBookIdWithCursor(String bookId, Long cursor, int limit,
            String sortField, String sortDirection) {

        String columnName = StringCaseUtils.camelToSnake(sortField);
        String comparison = "asc".equalsIgnoreCase(sortDirection) ? ">" : "<";
        String orderDirection = "asc".equalsIgnoreCase(sortDirection) ? "ASC" : "DESC";

        String sql = String.format(
                """
                        SELECT * FROM reviews r
                        WHERE (:cursor IS NULL OR
                            (r.%s %s (SELECT r2.%s FROM reviews r2 WHERE r2.id = :cursor) OR
                            (r.%s = (SELECT r2.%s FROM reviews r2 WHERE r2.id = :cursor) AND r.id > :cursor)))
                        AND r.book_id = :bookId
                        AND r.is_deleted = false
                        ORDER BY
                            r.%s %s,
                            r.id ASC
                        LIMIT :limit
                        """,
                columnName, comparison, columnName, columnName, columnName, columnName,
                orderDirection);

        Query query = entityManager.createNativeQuery(sql, Review.class);
        query.setParameter("bookId", bookId);
        query.setParameter("cursor", cursor);
        query.setParameter("limit", limit);

        @SuppressWarnings("unchecked")
        List<Review> result = query.getResultList();
        return result;
    }
}
