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

        // ソートするフィールド名（キャメルケース）をカラム名（スネークケース）に変換
        String columnName = StringCaseUtils.camelToSnake(sortField);

        String comparison = "asc".equalsIgnoreCase(sortDirection) ? ">" : "<";
        String orderDirection = "asc".equalsIgnoreCase(sortDirection) ? "ASC" : "DESC";

        String sql = String.format(
                """
                        SELECT * FROM bookmarks tbl
                        WHERE (:cursor IS NULL OR
                            (tbl.%s %s (SELECT tbl2.%s FROM bookmarks tbl2 WHERE tbl2.id = :cursor) OR
                            (tbl.%s = (SELECT tbl2.%s FROM bookmarks tbl2 WHERE tbl2.id = :cursor) AND tbl.id > :cursor)))
                        AND tbl.user_id = :userId
                        AND tbl.is_deleted = false
                        ORDER BY
                            tbl.%s %s,
                            tbl.id ASC
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
