package com.example.my_books_backend.entity.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.example.my_books_backend.util.StringCaseUtils;

/**
 * ソート可能なフィールドを一元管理するEnum SQLインジェクション対策のホワイトリストとしても機能
 */
public enum SortableField {

    // Book関連フィールド
    BOOK_TITLE("title", FieldCategory.BOOK),

    BOOK_PUBLICATION_DATE("publicationDate", FieldCategory.BOOK),

    BOOK_REVIEW_COUNT("reviewCount", FieldCategory.BOOK),

    BOOK_AVERAGE_RATING("averageRating", FieldCategory.BOOK),

    BOOK_POPULARITY("popularity", FieldCategory.BOOK),

    // Review関連フィールド
    REVIEW_UPDATED_AT("updatedAt", FieldCategory.REVIEW),

    REVIEW_CREATED_AT("createdAt", FieldCategory.REVIEW),

    REVIEW_RATING("rating", FieldCategory.REVIEW),

    // Favorite関連フィールド
    FAVORITE_UPDATED_AT("updatedAt", FieldCategory.FAVORITE),

    FAVORITE_CREATED_AT("createdAt", FieldCategory.FAVORITE),

    // Bookmark関連フィールド
    BOOKMARK_UPDATED_AT("updatedAt", FieldCategory.BOOKMARK),

    BOOKMARK_CREATED_AT("createdAt", FieldCategory.BOOKMARK),

    // 共通フィールド
    COMMON_ID("id", FieldCategory.COMMON);

    private final String camelCase;
    private final String snakeCase;
    private final FieldCategory category;

    SortableField(String camelCase, FieldCategory category) {
        this.camelCase = camelCase;
        this.snakeCase = StringCaseUtils.camelToSnake(camelCase);
        this.category = category;
    }

    public String getCamelCase() {
        return camelCase;
    }

    public String getSnakeCase() {
        return snakeCase;
    }

    public FieldCategory getCategory() {
        return category;
    }

    /**
     * フィールドカテゴリ
     */
    public enum FieldCategory {
        BOOK, REVIEW, FAVORITE, BOOKMARK, COMMON
    }

    // ===== ユーティリティメソッド =====

    /**
     * camelCaseからSortableFieldを取得
     */
    public static SortableField fromCamelCase(String camelCase, FieldCategory category) {
        return Arrays.stream(values())
                .filter(field -> field.camelCase.equals(camelCase)
                        && (field.category == category || field.category == FieldCategory.COMMON))
                .findFirst().orElse(null);
    }

    /**
     * snakeCaseからSortableFieldを取得
     */
    public static SortableField fromSnakeCase(String snakeCase, FieldCategory category) {
        return Arrays.stream(values())
                .filter(field -> field.snakeCase.equals(snakeCase)
                        && (field.category == category || field.category == FieldCategory.COMMON))
                .findFirst().orElse(null);
    }

    /**
     * 指定カテゴリの全camelCaseフィールド名を取得
     */
    public static List<String> getCamelCaseFields(FieldCategory category) {
        return Arrays.stream(values()).filter(
                field -> field.category == category || field.category == FieldCategory.COMMON)
                .map(field -> field.camelCase).collect(Collectors.toList());
    }

    /**
     * 指定カテゴリの全snakeCaseフィールド名を取得
     */
    public static Set<String> getSnakeCaseFields(FieldCategory category) {
        return Arrays.stream(values()).filter(
                field -> field.category == category || field.category == FieldCategory.COMMON)
                .map(field -> field.snakeCase).collect(Collectors.toSet());
    }

    /**
     * camelCaseをsnakeCaseに安全に変換（ホワイトリストチェック付き） StringCaseUtilsの変換ロジックを活用し、セキュリティチェックを追加
     */
    public static String toSnakeCaseSafely(String camelCase, FieldCategory category) {
        SortableField field = fromCamelCase(camelCase, category);
        if (field == null) {
            throw new IllegalArgumentException(String
                    .format("Invalid sort field '%s' for category '%s'", camelCase, category));
        }
        return field.snakeCase;
    }

    /**
     * フィールドが指定カテゴリで有効かチェック
     */
    public static boolean isValidField(String camelCase, FieldCategory category) {
        return fromCamelCase(camelCase, category) != null;
    }
}
