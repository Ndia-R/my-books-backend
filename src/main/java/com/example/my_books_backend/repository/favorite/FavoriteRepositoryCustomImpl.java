package com.example.my_books_backend.repository.favorite;

import java.util.List;
import com.example.my_books_backend.entity.Favorite;
import com.example.my_books_backend.util.StringCaseUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

public class FavoriteRepositoryCustomImpl implements FavoriteRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Favorite> findFavoritesByUserIdWithCursor(Long userId, Long cursor, int limit,
            String sortField, String sortDirection) {

        String columnName = StringCaseUtils.camelToSnake(sortField);
        String comparison = "asc".equalsIgnoreCase(sortDirection) ? ">" : "<";
        String orderDirection = "asc".equalsIgnoreCase(sortDirection) ? "ASC" : "DESC";

        String sql = String.format(
                """
                        SELECT * FROM favorites f
                        WHERE (:cursor IS NULL OR
                            (f.%s %s (SELECT f2.%s FROM favorites f2 WHERE f2.id = :cursor) OR
                            (f.%s = (SELECT f2.%s FROM favorites f2 WHERE f2.id = :cursor) AND f.id > :cursor)))
                        AND f.user_id = :userId
                        AND f.is_deleted = false
                        ORDER BY
                            f.%s %s,
                            f.id ASC
                        LIMIT :limit
                        """,
                columnName, comparison, columnName, columnName, columnName, columnName,
                orderDirection);

        Query query = entityManager.createNativeQuery(sql, Favorite.class);
        query.setParameter("userId", userId);
        query.setParameter("cursor", cursor);
        query.setParameter("limit", limit);

        @SuppressWarnings("unchecked")
        List<Favorite> result = query.getResultList();
        return result;
    }
}
