package com.example.my_books_backend.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import com.example.my_books_backend.entity.enums.SortableField.FieldCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

/**
 * カーソルベースクエリビルダー
 */
public class CursorQueryBuilder<T> {
    // エンティティ-テーブル-カテゴリの統合マッピング
    private static final Map<Class<?>, EntityConfig> ENTITY_CONFIG_MAP = Map.of(
        com.example.my_books_backend.entity.Book.class,
        new EntityConfig("books", "b", FieldCategory.BOOK),
        com.example.my_books_backend.entity.Bookmark.class,
        new EntityConfig("bookmarks", "bm", FieldCategory.BOOKMARK),
        com.example.my_books_backend.entity.Favorite.class,
        new EntityConfig("favorites", "f", FieldCategory.FAVORITE),
        com.example.my_books_backend.entity.Review.class,
        new EntityConfig("reviews", "r", FieldCategory.REVIEW)
    );

    private static class EntityConfig {
        final String tableName;
        final String alias;
        final FieldCategory category;

        EntityConfig(String tableName, String alias, FieldCategory category) {
            this.tableName = tableName;
            this.alias = alias;
            this.category = category;
        }
    }

    private final EntityManager entityManager;
    private final Class<T> entityClass;
    private final EntityConfig config;

    private final List<String> conditions = new ArrayList<>();
    private final Map<String, Object> parameters = new HashMap<>();

    private String sortField;
    private boolean isAscending = true;
    private Object cursor;
    private int limit = 20;

    private CursorQueryBuilder(EntityManager entityManager, Class<T> entityClass) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
        this.config = ENTITY_CONFIG_MAP.get(entityClass);

        if (config == null) {
            throw new IllegalArgumentException("Unsupported entity class: " + entityClass.getName());
        }
    }

    public static <T> CursorQueryBuilder<T> of(Class<T> entityClass, EntityManager entityManager) {
        return new CursorQueryBuilder<>(entityManager, entityClass);
    }

    public CursorQueryBuilder<T> filterByUser(Long userId) {
        RepositorySecurityUtils.validateUserId(userId);
        conditions.add(config.alias + ".user_id = :userId");
        parameters.put("userId", userId);
        return this;
    }

    public CursorQueryBuilder<T> filterByBook(String bookId) {
        RepositorySecurityUtils.validateBookId(bookId);
        conditions.add(config.alias + ".book_id = :bookId");
        parameters.put("bookId", bookId);
        return this;
    }

    public CursorQueryBuilder<T> filterByTitleKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Title keyword cannot be null or empty");
        }
        conditions.add(config.alias + ".title LIKE :titleKeyword");
        parameters.put("titleKeyword", "%" + keyword.trim() + "%");
        return this;
    }

    public CursorQueryBuilder<T> filterByGenresOr(List<Long> genreIds) {
        return filterByGenres(genreIds, false);
    }

    public CursorQueryBuilder<T> filterByGenresAnd(List<Long> genreIds) {
        return filterByGenres(genreIds, true);
    }

    public CursorQueryBuilder<T> withCursor(Object cursor) {
        this.cursor = cursor;
        return this;
    }

    public CursorQueryBuilder<T> withLimit(Integer limit) {
        this.limit = RepositorySecurityUtils.validateLimit(limit);
        return this;
    }

    public CursorQueryBuilder<T> orderBy(String sortString) {
        if (sortString == null || sortString.trim().isEmpty()) {
            throw new IllegalArgumentException("Sort string cannot be null or empty");
        }

        String[] parts = sortString.split("\\.");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Sort string must be in format 'field.direction' (e.g., 'title.asc')");
        }

        this.sortField = RepositorySecurityUtils.validateAndGetColumnName(parts[0], config.category);
        this.isAscending = RepositorySecurityUtils.validateSortDirection(parts[1]);
        return this;
    }

    public Query build() {
        if (sortField == null) {
            throw new IllegalStateException("Sort field must be specified");
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ").append(config.tableName).append(" ").append(config.alias);

        List<String> whereClause = new ArrayList<>(conditions);
        whereClause.add(config.alias + ".is_deleted = false");

        // カーソル条件
        if (cursor != null) {
            String operator = isAscending ? ">" : "<";
            whereClause.add(
                "(" + config.alias + "." + sortField + " " + operator + " " +
                    "(SELECT " + sortField + " FROM " + config.tableName + " WHERE id = :cursor)" +
                    " OR (" + config.alias + "." + sortField + " = " +
                    "(SELECT " + sortField + " FROM " + config.tableName + " WHERE id = :cursor)" +
                    " AND " + config.alias + ".id > :cursor))"
            );
            parameters.put("cursor", cursor);
        }

        sql.append(" WHERE ").append(String.join(" AND ", whereClause));

        String direction = isAscending ? "ASC" : "DESC";
        sql.append(" ORDER BY ").append(config.alias).append(".").append(sortField).append(" ").append(direction);
        sql.append(", ").append(config.alias).append(".id ASC");
        sql.append(" LIMIT :limit");

        Query query = entityManager.createNativeQuery(sql.toString(), entityClass);

        parameters.forEach(query::setParameter);
        query.setParameter("limit", limit);

        return query;
    }

    // ジャンルフィルターのプライベート実装
    private CursorQueryBuilder<T> filterByGenres(List<Long> genreIds, boolean isAndCondition) {
        RepositorySecurityUtils.validateGenreIds(genreIds);

        String placeholders = createPlaceholders("genreId", genreIds.size());

        if (isAndCondition) {
            conditions.add(
                config.alias + ".id IN (" +
                    "SELECT bg.book_id FROM book_genres bg " +
                    "WHERE bg.genre_id IN (" + placeholders + ") " +
                    "GROUP BY bg.book_id HAVING COUNT(DISTINCT bg.genre_id) = :genreCount)"
            );
            parameters.put("genreCount", genreIds.size());
        } else {
            conditions.add(
                config.alias + ".id IN (" +
                    "SELECT DISTINCT bg.book_id FROM book_genres bg " +
                    "WHERE bg.genre_id IN (" + placeholders + "))"
            );
        }

        for (int i = 0; i < genreIds.size(); i++) {
            parameters.put("genreId" + i, genreIds.get(i));
        }
        return this;
    }

    private String createPlaceholders(String prefix, int count) {
        if (count <= 0 || count > 50) {
            throw new IllegalArgumentException("Invalid count: " + count);
        }

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0)
                placeholders.append(", ");
            placeholders.append(":").append(prefix).append(i);
        }
        return placeholders.toString();
    }
}
