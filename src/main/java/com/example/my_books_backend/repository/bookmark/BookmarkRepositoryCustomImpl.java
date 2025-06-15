package com.example.my_books_backend.repository.bookmark;

import java.util.List;
import com.example.my_books_backend.entity.Bookmark;
import com.example.my_books_backend.util.StringCaseUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

public class BookmarkRepositoryCustomImpl implements BookmarkRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Bookmark> findBookmarksByUserIdWithCursor(Long userId, Long cursor, int limit,
            String sortField, String sortDirection) {

        String columnName = StringCaseUtils.camelToSnake(sortField);
        String comparison = "asc".equalsIgnoreCase(sortDirection) ? ">" : "<";
        String orderDirection = "asc".equalsIgnoreCase(sortDirection) ? "ASC" : "DESC";

        String sql = String.format(
                """
                        SELECT * FROM bookmarks b
                        WHERE (:cursor IS NULL OR
                            (b.%s %s (SELECT b2.%s FROM bookmarks b2 WHERE b2.id = :cursor) OR
                            (b.%s = (SELECT b2.%s FROM bookmarks b2 WHERE b2.id = :cursor) AND b.id > :cursor)))
                        AND b.user_id = :userId
                        AND b.is_deleted = false
                        ORDER BY
                            b.%s %s,
                            b.id ASC
                        LIMIT :limit
                        """,
                columnName, comparison, columnName, columnName, columnName, columnName,
                orderDirection);

        Query query = entityManager.createNativeQuery(sql, Bookmark.class);
        query.setParameter("userId", userId);
        query.setParameter("cursor", cursor);
        query.setParameter("limit", limit);

        @SuppressWarnings("unchecked")
        List<Bookmark> result = query.getResultList();
        return result;
    }
}
