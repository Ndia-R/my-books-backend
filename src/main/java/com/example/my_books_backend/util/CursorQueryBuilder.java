package com.example.my_books_backend.util;

import java.util.List;
import com.example.my_books_backend.entity.enums.SortableField.FieldCategory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

/**
 * Fluent Builder Pattern によるカーソルベースクエリ構築
 * 動的クエリ生成とパラメータ設定を一元化
 */
public class CursorQueryBuilder {

    private final EntityManager entityManager;
    private final Class<?> entityClass;
    private String tableName;
    private String tableAlias;
    private FieldCategory fieldCategory;

    // クエリ条件
    private String baseSelect;
    private String joins = "";
    private String whereConditions = "";
    private String sortField;
    private String sortDirection;
    private Object cursor;
    private Integer limit;

    // ユーザー・書籍フィルター
    private Long userId;
    private String bookId;
    private List<Long> genreIds;
    private Boolean isAndCondition;
    private String titleKeyword;

    private CursorQueryBuilder(EntityManager entityManager, Class<?> entityClass) {
        this.entityManager = entityManager;
        this.entityClass = entityClass;
    }

    /**
     * エンティティクラスを指定してビルダーを開始
     */
    public static CursorQueryBuilder forEntity(Class<?> entityClass, EntityManager entityManager) {
        return new CursorQueryBuilder(entityManager, entityClass);
    }

    /**
     * テーブル設定（Book用）
     */
    public CursorQueryBuilder fromBooks() {
        this.tableName = "books";
        this.tableAlias = "b";
        this.fieldCategory = FieldCategory.BOOK;
        this.baseSelect = "SELECT * FROM books b";
        return this;
    }

    /**
     * テーブル設定（Bookmark用）
     */
    public CursorQueryBuilder fromBookmarks() {
        this.tableName = "bookmarks";
        this.tableAlias = "b";
        this.fieldCategory = FieldCategory.BOOKMARK;
        this.baseSelect = "SELECT * FROM bookmarks b";
        return this;
    }

    /**
     * テーブル設定（Favorite用）
     */
    public CursorQueryBuilder fromFavorites() {
        this.tableName = "favorites";
        this.tableAlias = "f";
        this.fieldCategory = FieldCategory.FAVORITE;
        this.baseSelect = "SELECT * FROM favorites f";
        return this;
    }

    /**
     * テーブル設定（Review用）
     */
    public CursorQueryBuilder fromReviews() {
        this.tableName = "reviews";
        this.tableAlias = "r";
        this.fieldCategory = FieldCategory.REVIEW;
        this.baseSelect = "SELECT * FROM reviews r";
        return this;
    }

    /**
     * ユーザーIDでフィルター
     */
    public CursorQueryBuilder filterByUser(Long userId) {
        RepositorySecurityUtils.validateUserId(userId);
        this.userId = userId;
        addWhereCondition(tableAlias + ".user_id = :userId");
        return this;
    }

    /**
     * 書籍IDでフィルター
     */
    public CursorQueryBuilder filterByBook(String bookId) {
        RepositorySecurityUtils.validateBookId(bookId);
        this.bookId = bookId;
        addWhereCondition(tableAlias + ".book_id = :bookId");
        return this;
    }

    /**
     * タイトルキーワードでフィルター
     */
    public CursorQueryBuilder filterByTitleKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("Title keyword cannot be null or empty");
        }
        this.titleKeyword = keyword;
        addWhereCondition(tableAlias + ".title LIKE :titleKeyword");
        return this;
    }

    /**
     * ジャンルでフィルター（OR条件）
     */
    public CursorQueryBuilder filterByGenresOr(List<Long> genreIds) {
        return filterByGenres(genreIds, false);
    }

    /**
     * ジャンルでフィルター（AND条件）
     */
    public CursorQueryBuilder filterByGenresAnd(List<Long> genreIds) {
        return filterByGenres(genreIds, true);
    }

    /**
     * ジャンルでフィルター
     */
    private CursorQueryBuilder filterByGenres(List<Long> genreIds, boolean isAndCondition) {
        RepositorySecurityUtils.validateGenreIds(genreIds);
        this.genreIds = genreIds;
        this.isAndCondition = isAndCondition;

        // JOIN句の追加
        this.joins += "\nJOIN book_genres bg ON " + tableAlias + ".id = bg.book_id";
        this.joins += "\nJOIN genres g ON bg.genre_id = g.id";

        // ジャンル条件の追加
        String genreCondition = generateGenreCondition(genreIds);
        addWhereCondition(genreCondition);

        // AND条件の場合はSELECTとGROUP BY/HAVINGを調整
        if (isAndCondition) {
            // DISTINCT削除、GROUP BY/HAVING追加は後でbuild()時に処理
        } else {
            // OR条件の場合はDISTINCTを使用
            this.baseSelect = "SELECT DISTINCT " + tableAlias + ".* FROM " + tableName + " " + tableAlias;
        }

        return this;
    }

    /**
     * カーソル設定
     */
    public CursorQueryBuilder withCursor(Object cursor) {
        if (cursor != null) {
            if (cursor instanceof String) {
                RepositorySecurityUtils.validateBookId((String) cursor);
            } else if (cursor instanceof Long) {
                RepositorySecurityUtils.validateCursor((Long) cursor);
            }
        }
        this.cursor = cursor;
        return this;
    }

    /**
     * リミット設定
     */
    public CursorQueryBuilder withLimit(Integer limit) {
        this.limit = RepositorySecurityUtils.validateLimit(limit);
        return this;
    }

    /**
     * ソート設定
     */
    public CursorQueryBuilder orderBy(String sortField, String sortDirection, FieldCategory category) {
        // セキュリティチェックはbuild()時に実行
        this.sortField = sortField;
        this.sortDirection = sortDirection;
        this.fieldCategory = category;
        return this;
    }

    /**
     * クエリを構築して実行可能なQueryオブジェクトを返す
     */
    public Query build() {
        // セキュリティチェック
        String columnName = RepositorySecurityUtils.validateAndGetColumnName(sortField, fieldCategory);
        boolean isAsc = RepositorySecurityUtils.validateSortDirection(sortDirection);

        String comparison = isAsc ? ">" : "<";
        String orderDirection = isAsc ? "ASC" : "DESC";

        // カーソル条件の構築
        String cursorCondition = buildCursorCondition(columnName, comparison);

        // WHERE句の完成
        String finalWhereClause = cursorCondition;
        if (!whereConditions.isEmpty()) {
            finalWhereClause += " AND " + whereConditions;
        }
        finalWhereClause += " AND " + tableAlias + ".is_deleted = false";

        // GROUP BY/HAVING句（AND条件のジャンル検索用）
        String groupByHaving = "";
        if (genreIds != null && isAndCondition != null && isAndCondition) {
            groupByHaving = "\nGROUP BY " + tableAlias + ".id, " + tableAlias + "." + columnName;
            groupByHaving += "\nHAVING COUNT(DISTINCT g.id) = :genreCount";
        }

        // ORDER BY句
        String orderByClause = "\nORDER BY " + tableAlias + "." + columnName + " " + orderDirection + ", " + tableAlias
            + ".id ASC";

        // 最終SQL構築
        String sql = baseSelect + joins + "\nWHERE " + finalWhereClause + groupByHaving + orderByClause
            + "\nLIMIT :limit";

        // Queryオブジェクト作成とパラメータ設定
        Query query = entityManager.createNativeQuery(sql, entityClass);

        // 共通パラメータ
        query.setParameter("cursor", cursor);
        query.setParameter("limit", limit);

        // 条件別パラメータ設定
        if (userId != null) {
            query.setParameter("userId", userId);
        }
        if (bookId != null) {
            query.setParameter("bookId", bookId);
        }
        if (titleKeyword != null) {
            query.setParameter("titleKeyword", "%" + titleKeyword + "%");
        }
        if (genreIds != null) {
            setGenreIdParameters(query, genreIds);
            if (isAndCondition != null && isAndCondition) {
                query.setParameter("genreCount", genreIds.size());
            }
        }

        return query;
    }

    // ===== プライベートヘルパーメソッド =====

    private void addWhereCondition(String condition) {
        if (!whereConditions.isEmpty()) {
            whereConditions += " AND ";
        }
        whereConditions += condition;
    }

    private String buildCursorCondition(String columnName, String comparison) {
        // WITH句やJOINを使ってサブクエリを1回に削減
        return String.format(
            "(:cursor IS NULL OR " +
                "EXISTS (SELECT 1 FROM %s cursor_ref WHERE cursor_ref.id = :cursor AND " +
                "(%s.%s %s cursor_ref.%s OR " +
                "(%s.%s = cursor_ref.%s AND %s.id > :cursor))))",
            tableName,
            tableAlias,
            columnName,
            comparison,
            columnName,
            tableAlias,
            columnName,
            columnName,
            tableAlias
        );
    }

    private String generateGenreCondition(List<Long> genreIds) {
        String placeholders = generateGenreIdPlaceholders(genreIds.size());
        return "g.id IN (" + placeholders + ")";
    }

    private String generateGenreIdPlaceholders(int genreCount) {
        if (genreCount <= 0) {
            throw new IllegalArgumentException("Genre count must be positive: " + genreCount);
        }
        if (genreCount > 50) {
            throw new IllegalArgumentException("Too many genres (max: 50): " + genreCount);
        }

        return java.util.stream.IntStream
            .range(0, genreCount)
            .mapToObj(i -> ":genreId" + i)
            .collect(java.util.stream.Collectors.joining(","));
    }

    private void setGenreIdParameters(Query query, List<Long> genreIds) {
        for (int i = 0; i < genreIds.size(); i++) {
            query.setParameter("genreId" + i, genreIds.get(i));
        }
    }
}
