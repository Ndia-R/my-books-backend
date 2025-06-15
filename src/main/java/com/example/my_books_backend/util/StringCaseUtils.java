package com.example.my_books_backend.util;

/**
 * 文字列のケース変換を行うユーティリティクラス キャメルケース、スネークケース間の変換機能を提供します
 */
public final class StringCaseUtils {

    /**
     * ユーティリティクラスなのでインスタンス化を防ぐ
     */
    private StringCaseUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * キャメルケースの文字列をスネークケースに変換する
     * 
     * @param camelCase 変換対象のキャメルケース文字列（例: "userName", "firstName"）
     * @return スネークケースに変換された文字列（例: "user_name", "first_name"） 入力がnullまたは空文字列の場合は、そのまま返す
     * 
     * @example StringCaseUtils.camelToSnake("userName") → "user_name"
     *          StringCaseUtils.camelToSnake("firstName") → "first_name"
     *          StringCaseUtils.camelToSnake("ID") → "i_d"
     *          StringCaseUtils.camelToSnake("XMLHttpRequest") → "x_m_l_http_request"
     */
    public static String camelToSnake(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < camelCase.length(); i++) {
            char currentChar = camelCase.charAt(i);

            // 大文字の場合の処理
            if (Character.isUpperCase(currentChar)) {
                // 最初の文字でない場合、アンダースコアを追加
                if (i > 0) {
                    // 連続する大文字の処理を考慮
                    // 例: "XMLHttpRequest" → "x_m_l_http_request"
                    char prevChar = camelCase.charAt(i - 1);
                    if (!Character.isUpperCase(prevChar)) {
                        result.append('_');
                    } else if (i + 1 < camelCase.length()) {
                        char nextChar = camelCase.charAt(i + 1);
                        if (Character.isLowerCase(nextChar)) {
                            result.append('_');
                        }
                    }
                }
                result.append(Character.toLowerCase(currentChar));
            } else {
                result.append(currentChar);
            }
        }

        return result.toString();
    }

    /**
     * スネークケースの文字列をキャメルケースに変換する
     * 
     * @param snakeCase 変換対象のスネークケース文字列（例: "user_name", "first_name"）
     * @return キャメルケースに変換された文字列（例: "userName", "firstName"） 入力がnullまたは空文字列の場合は、そのまま返す
     * 
     * @example StringCaseUtils.snakeToCamel("user_name") → "userName"
     *          StringCaseUtils.snakeToCamel("first_name") → "firstName"
     *          StringCaseUtils.snakeToCamel("_leading_underscore") → "LeadingUnderscore"
     *          StringCaseUtils.snakeToCamel("trailing_underscore_") → "trailingUnderscore"
     */
    public static String snakeToCamel(String snakeCase) {
        if (snakeCase == null || snakeCase.isEmpty()) {
            return snakeCase;
        }

        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = false;

        for (int i = 0; i < snakeCase.length(); i++) {
            char currentChar = snakeCase.charAt(i);

            if (currentChar == '_') {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    result.append(Character.toUpperCase(currentChar));
                    capitalizeNext = false;
                } else {
                    result.append(Character.toLowerCase(currentChar));
                }
            }
        }

        return result.toString();
    }

    /**
     * スネークケースの文字列をパスカルケース（先頭大文字のキャメルケース）に変換する
     * 
     * @param snakeCase 変換対象のスネークケース文字列（例: "user_name"）
     * @return パスカルケースに変換された文字列（例: "UserName"） 入力がnullまたは空文字列の場合は、そのまま返す
     * 
     * @example StringCaseUtils.snakeToPascal("user_name") → "UserName"
     *          StringCaseUtils.snakeToPascal("first_name") → "FirstName"
     */
    public static String snakeToPascal(String snakeCase) {
        if (snakeCase == null || snakeCase.isEmpty()) {
            return snakeCase;
        }

        String camelCase = snakeToCamel(snakeCase);
        if (camelCase.isEmpty()) {
            return camelCase;
        }

        return Character.toUpperCase(camelCase.charAt(0)) + camelCase.substring(1);
    }

    /**
     * パスカルケースの文字列をスネークケースに変換する
     * 
     * @param pascalCase 変換対象のパスカルケース文字列（例: "UserName"）
     * @return スネークケースに変換された文字列（例: "user_name"）
     * 
     * @example StringCaseUtils.pascalToSnake("UserName") → "user_name"
     *          StringCaseUtils.pascalToSnake("FirstName") → "first_name"
     */
    public static String pascalToSnake(String pascalCase) {
        return camelToSnake(pascalCase);
    }

    /**
     * 文字列が有効なキャメルケース形式かどうかを判定する
     * 
     * @param str 判定対象の文字列
     * @return キャメルケース形式の場合true、そうでなければfalse
     */
    public static boolean isCamelCase(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        // 最初の文字は小文字である必要がある
        if (!Character.isLowerCase(str.charAt(0))) {
            return false;
        }

        // アンダースコアやハイフンが含まれていない
        if (str.contains("_") || str.contains("-")) {
            return false;
        }

        // 文字と数字のみで構成されている
        return str.matches("^[a-zA-Z][a-zA-Z0-9]*$");
    }

    /**
     * 文字列が有効なスネークケース形式かどうかを判定する
     * 
     * @param str 判定対象の文字列
     * @return スネークケース形式の場合true、そうでなければfalse
     */
    public static boolean isSnakeCase(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }

        // 小文字、数字、アンダースコアのみで構成されている
        // 連続するアンダースコアは無効
        return str.matches("^[a-z][a-z0-9]*(_[a-z0-9]+)*$");
    }
}
