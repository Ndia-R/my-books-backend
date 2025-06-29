package com.example.my_books_backend.util;

import java.util.List;
import com.example.my_books_backend.entity.enums.SortableField;
import com.example.my_books_backend.entity.enums.SortableField.FieldCategory;

/**
 * Repository層におけるセキュリティ対策の共通ユーティリティ SQLインジェクション防止、入力値検証等の機能を提供
 */
public final class RepositorySecurityUtils {

    private RepositorySecurityUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * ✅ SQLインジェクション対策：フィールド名の厳格なバリデーション Enumベースのホワイトリストでカラム名を検証し、安全なsnake_caseを返す
     * 
     * @param sortField ソート対象フィールド（camelCase）
     * @param category  フィールドカテゴリ
     * @return 検証済みのsnake_caseカラム名
     * @throws IllegalArgumentException 無効なフィールドの場合
     */
    public static String validateAndGetColumnName(String sortField, FieldCategory category) {
        try {
            return SortableField.toSnakeCaseSafely(sortField, category);
        } catch (IllegalArgumentException e) {
            // セキュリティログ出力（実際のプロダクションでは適切なロガーを使用）
            throw new IllegalArgumentException(
                String.format(
                    "Security violation: Invalid sort field '%s' for category '%s'",
                    sortField,
                    category
                ),
                e
            );
        }
    }

    /**
     * ✅ ソート方向のバリデーション
     * 
     * @param sortDirection ソート方向文字列
     * @return ASCの場合true、DESCの場合false
     */
    public static boolean validateSortDirection(String sortDirection) {
        if (sortDirection == null) {
            return true; // デフォルトはASC
        }
        String normalized = sortDirection.trim().toLowerCase();
        if ("asc".equals(normalized)) {
            return true;
        } else if ("desc".equals(normalized)) {
            return false;
        } else {
            throw new IllegalArgumentException(
                String.format(
                    "Security violation: Invalid sort direction '%s'. Only 'asc' or 'desc' allowed.",
                    sortDirection
                )
            );
        }
    }

    /**
     * ✅ ユーザーIDのバリデーション
     */
    public static void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID: " + userId);
        }
    }

    /**
     * ✅ 書籍IDのバリデーション
     */
    public static void validateBookId(String bookId) {
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new IllegalArgumentException("Book ID cannot be null or empty");
        }
        if (bookId.length() > 255) { // より適切な最大長に調整
            throw new IllegalArgumentException("Book ID too long: " + bookId.length());
        }
        // より柔軟な文字許可（日本語やUnicode文字を含む）
        // 制御文字、改行文字、タブ文字は除外
        if (bookId.matches(".*[\\p{Cntrl}\\r\\n\\t].*")) {
            throw new IllegalArgumentException("Book ID contains invalid control characters");
        }
    }

    /**
     * ✅ カーソルIDのバリデーション
     */
    public static void validateCursor(Long cursor) {
        if (cursor != null && cursor <= 0) {
            throw new IllegalArgumentException("Invalid cursor ID: " + cursor);
        }
    }

    /**
     * ✅ リミット値のバリデーション（DoS攻撃対策）
     */
    public static int validateLimit(int limit) {
        if (limit <= 0) {
            throw new IllegalArgumentException("Limit must be positive: " + limit);
        }
        if (limit > 1000) { // DoS攻撃対策
            throw new IllegalArgumentException("Limit too large (max: 1000): " + limit);
        }
        return limit;
    }

    /**
     * ✅ ジャンルIDリストのバリデーション
     */
    public static void validateGenreIds(List<Long> genreIds) {
        if (genreIds == null) {
            throw new IllegalArgumentException("Genre IDs cannot be null");
        }

        // 最大数制限（DoS攻撃対策）
        if (genreIds.size() > 50) {
            throw new IllegalArgumentException("Too many genre IDs specified (max: 50)");
        }

        // NULL値・無効値チェック
        for (int i = 0; i < genreIds.size(); i++) {
            Long id = genreIds.get(i);
            if (id == null || id <= 0) {
                throw new IllegalArgumentException(
                    String.format("Invalid genre ID at index %d: %s", i, id)
                );
            }
        }
    }
}
