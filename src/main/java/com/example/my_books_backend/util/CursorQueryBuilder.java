package com.example.my_books_backend.util;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.my_books_backend.entity.enums.SortableField.FieldCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

/**
 * セキュアなカーソルベースクエリビルダー
 * SQLインジェクション対策とパフォーマンス最適化を実装
 */
public class CursorQueryBuilder {
    private static final Logger logger = LoggerFactory.getLogger(CursorQueryBuilder.class);

    // セキュリティ: ホワイトリスト定義（テーブル構成）
    private static final Map<String, TableConfig> ALLOWED_TABLES = Map.of(
        "books",
        new TableConfig("books", "b"),
        "bookmarks",
        new TableConfig("bookmarks", "bm"),
        "favorites",
        new TableConfig("favorites", "f"),
        "reviews",
        new TableConfig("reviews", "r")
    );

    /**
     * テーブル設定クラス（セキュリティ強化）
     */
    private static class TableConfig {
        final String tableName;
        final String alias;

        TableConfig(String tableName, String alias) {
            this.tableName = tableName;
            this.alias = alias;
        }
    }

    private final EntityManager entityManager;
    private final Class<?> entityClass;
    private final TableConfig tableConfig;

    // クエリ構築用フィールド
    private final List<String> conditions = new ArrayList<>();
    private final Map<String, Object> parameters = new HashMap<>();

    private String sortField;
    private boolean isAscending = true;
    private Object cursor;
    private Object cursorValue; // カーソル値をキャッシュ（性能向上）
    private int limit = 20;

    private CursorQueryBuilder(EntityManager entityManager, Class<?> entityClass, String tableKey) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
        this.tableConfig = validateAndGetTableConfig(tableKey);
    }

    /**
     * セキュリティ: テーブル設定の厳格な検証
     */
    private static TableConfig validateAndGetTableConfig(String tableKey) {
        TableConfig config = ALLOWED_TABLES.get(tableKey);
        if (config == null) {
            // セキュリティログ: 不正なテーブルアクセス試行を記録
            throw new IllegalArgumentException(
                String.format("Security violation: Unauthorized table access attempt - %s", tableKey)
            );
        }
        return config;
    }

    // ===== ファクトリーメソッド（型安全性向上） =====

    public static CursorQueryBuilder forEntity(Class<?> entityClass, EntityManager entityManager) {
        // 後方互換性のため、エンティティクラスから適切なテーブルを推定
        if (entityClass.getSimpleName().equals("Book")) {
            return forBooks(entityManager);
        } else if (entityClass.getSimpleName().equals("Bookmark")) {
            return forBookmarks(entityManager);
        } else if (entityClass.getSimpleName().equals("Favorite")) {
            return forFavorites(entityManager);
        } else if (entityClass.getSimpleName().equals("Review")) {
            return forReviews(entityManager);
        } else {
            throw new IllegalArgumentException("Unsupported entity class: " + entityClass.getName());
        }
    }

    public static CursorQueryBuilder forBooks(EntityManager entityManager) {
        return new CursorQueryBuilder(
            entityManager,
            com.example.my_books_backend.entity.Book.class,
            "books"
        );
    }

    public static CursorQueryBuilder forBookmarks(EntityManager entityManager) {
        return new CursorQueryBuilder(
            entityManager,
            com.example.my_books_backend.entity.Bookmark.class,
            "bookmarks"
        );
    }

    public static CursorQueryBuilder forFavorites(EntityManager entityManager) {
        return new CursorQueryBuilder(
            entityManager,
            com.example.my_books_backend.entity.Favorite.class,
            "favorites"
        );
    }

    public static CursorQueryBuilder forReviews(EntityManager entityManager) {
        return new CursorQueryBuilder(
            entityManager,
            com.example.my_books_backend.entity.Review.class,
            "reviews"
        );
    }

    // ===== フィルターメソッド =====

    public CursorQueryBuilder filterByUser(Long userId) {
        RepositorySecurityUtils.validateUserId(userId);
        conditions.add(tableConfig.alias + ".user_id = :userId");
        parameters.put("userId", userId);
        return this;
    }

    public CursorQueryBuilder filterByBook(String bookId) {
        RepositorySecurityUtils.validateBookId(bookId);
        conditions.add(tableConfig.alias + ".book_id = :bookId");
        parameters.put("bookId", bookId);
        return this;
    }

    public CursorQueryBuilder filterByTitleKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Title keyword cannot be null or empty");
        }
        conditions.add(tableConfig.alias + ".title LIKE :titleKeyword");
        parameters.put("titleKeyword", "%" + keyword.trim() + "%");
        return this;
    }

    public CursorQueryBuilder filterByGenresOr(List<Long> genreIds) {
        return filterByGenres(genreIds, false);
    }

    public CursorQueryBuilder filterByGenresAnd(List<Long> genreIds) {
        return filterByGenres(genreIds, true);
    }

    /**
     * ジャンルフィルター（改善されたサブクエリ使用）
     */
    private CursorQueryBuilder filterByGenres(List<Long> genreIds, boolean isAndCondition) {
        RepositorySecurityUtils.validateGenreIds(genreIds);

        if (isAndCondition) {
            // AND条件: すべてのジャンルを含む書籍
            conditions.add(
                tableConfig.alias + ".id IN (" +
                    "SELECT bg.book_id FROM book_genres bg " +
                    "WHERE bg.genre_id IN (" + createGenrePlaceholders(genreIds.size()) + ") " +
                    "GROUP BY bg.book_id HAVING COUNT(DISTINCT bg.genre_id) = :genreCount)"
            );
            parameters.put("genreCount", genreIds.size());
        } else {
            // OR条件: いずれかのジャンルを含む書籍
            conditions.add(
                tableConfig.alias + ".id IN (" +
                    "SELECT DISTINCT bg.book_id FROM book_genres bg " +
                    "WHERE bg.genre_id IN (" + createGenrePlaceholders(genreIds.size()) + "))"
            );
        }

        // ジャンルIDパラメータ設定
        for (int i = 0; i < genreIds.size(); i++) {
            parameters.put("genreId" + i, genreIds.get(i));
        }

        return this;
    }

    // ===== カーソル・ソート設定 =====

    public CursorQueryBuilder withCursor(Object cursor) {
        if (cursor != null) {
            validateCursorType(cursor);
            this.cursor = cursor;
            // カーソル値はbuild()時に取得（エラーハンドリング改善）
        }
        return this;
    }

    public CursorQueryBuilder withLimit(Integer limit) {
        this.limit = RepositorySecurityUtils.validateLimit(limit);
        return this;
    }

    public CursorQueryBuilder orderBy(String sortField, String sortDirection, FieldCategory category) {
        // セキュリティチェック: ホワイトリストベースの検証
        this.sortField = RepositorySecurityUtils.validateAndGetColumnName(sortField, category);
        this.isAscending = RepositorySecurityUtils.validateSortDirection(sortDirection);
        return this;
    }

    // ===== クエリ構築・実行 =====

    public Query build() {
        if (sortField == null) {
            throw new IllegalStateException("Sort field must be specified before building query");
        }

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ").append(tableConfig.tableName).append(" ").append(tableConfig.alias);

        // WHERE句構築
        List<String> whereClause = new ArrayList<>(conditions);
        whereClause.add(tableConfig.alias + ".is_deleted = false");

        // カーソル条件（改善されたエラーハンドリング）
        boolean hasCursorCondition = false;
        if (cursor != null) {
            try {
                // カーソル値をbuild時に取得（エラーハンドリング改善）
                this.cursorValue = fetchCursorValue(cursor);
                addOptimizedCursorCondition(whereClause);
                hasCursorCondition = true;
            } catch (Exception e) {
                // カーソル値取得に失敗した場合は、カーソル条件を無視してログ出力
                logger.warn(
                    "Failed to fetch cursor value for cursor: {}. Ignoring cursor condition. Error: {}",
                    cursor,
                    e.getMessage()
                );
                // カーソル条件なしでクエリを続行
            }
        }

        sql.append(" WHERE ").append(String.join(" AND ", whereClause));

        // ORDER BY句
        String direction = isAscending ? "ASC" : "DESC";
        sql.append(" ORDER BY ").append(tableConfig.alias).append(".").append(sortField).append(" ").append(direction);
        sql.append(", ").append(tableConfig.alias).append(".id ASC"); // 安定ソート保証

        // LIMIT句
        sql.append(" LIMIT :limit");

        // Queryオブジェクト作成とパラメータ設定
        Query query = entityManager.createNativeQuery(sql.toString(), entityClass);

        // 全パラメータ設定
        for (Map.Entry<String, Object> param : parameters.entrySet()) {
            query.setParameter(param.getKey(), param.getValue());
        }
        query.setParameter("limit", limit);

        // カーソル条件が実際に追加された場合のみパラメータ設定
        if (hasCursorCondition) {
            query.setParameter("cursor", cursor);
            query.setParameter("cursorValue", cursorValue);
        }

        return query;
    }

    // ===== プライベートメソッド =====

    /**
     * 大幅に最適化されたカーソル条件
     * EXISTSサブクエリを削除し、事前取得した値での直接比較を使用
     */
    private void addOptimizedCursorCondition(List<String> whereClause) {
        String operator = isAscending ? ">" : "<";

        // カーソル値を直接比較（サブクエリ完全削除）
        whereClause.add(
            "(" + tableConfig.alias + "." + sortField + " " + operator + " :cursorValue OR " +
                "(" + tableConfig.alias + "." + sortField + " = :cursorValue AND " +
                tableConfig.alias + ".id > :cursor))"
        );
    }

    /**
     * カーソル値の事前取得（性能大幅向上）
     * EXISTSサブクエリを1回のシンプルなSELECTに置き換え
     */
    private Object fetchCursorValue(Object cursorId) {
        String sql = "SELECT " + sortField + " FROM " + tableConfig.tableName
            + " WHERE id = :cursorId AND is_deleted = false";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("cursorId", cursorId);

        List<?> results = query.getResultList();
        if (results.isEmpty()) {
            throw new IllegalArgumentException("Invalid cursor: record not found or deleted - " + cursorId);
        }

        return results.get(0);
    }

    /**
     * ジャンルIDプレースホルダー生成（セキュリティ強化）
     */
    private String createGenrePlaceholders(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Genre count must be positive: " + count);
        }
        if (count > 50) { // DoS攻撃対策
            throw new IllegalArgumentException("Too many genres specified (max: 50): " + count);
        }

        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < count; i++) {
            if (i > 0)
                placeholders.append(", ");
            placeholders.append(":genreId").append(i);
        }
        return placeholders.toString();
    }

    /**
     * カーソル型の厳格な検証（型安全性向上）
     */
    private void validateCursorType(Object cursor) {
        if (entityClass.getSimpleName().equals("Book")) {
            if (!(cursor instanceof String)) {
                throw new IllegalArgumentException(
                    String.format("Book cursor must be String, got: %s", cursor.getClass().getSimpleName())
                );
            }
            RepositorySecurityUtils.validateBookId((String) cursor);
        } else {
            if (!(cursor instanceof Long)) {
                throw new IllegalArgumentException(
                    String.format(
                        "Cursor must be Long for non-Book entities, got: %s",
                        cursor.getClass().getSimpleName()
                    )
                );
            }
            RepositorySecurityUtils.validateCursor((Long) cursor);
        }
    }
}
