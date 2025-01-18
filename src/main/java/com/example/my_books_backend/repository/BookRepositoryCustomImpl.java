package com.example.my_books_backend.repository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import com.example.my_books_backend.entity.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class BookRepositoryCustomImpl implements BookRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    // // 基本的な呼び出し
    // List<String> genres = Arrays.asList("ミステリー", "サスペンス");
    // Pageable pageable = PageRequest.of(0, 10); // 1ページ目、10件ずつ
    // Page<Book> result = bookRepository.findByGenreIds(genres, pageable);

    // // ソート条件付きの呼び出し
    // Pageable pageableWithSort = PageRequest.of(
    // 0,
    // 10,
    // Sort.by(Sort.Direction.DESC, "publishedDate")
    // );
    // Page<Book> sortedResult = bookRepository.findByGenreIds(genres,
    // pageableWithSort);

    @Override
    public Page<Book> findByGenreIds(String genreIdsParam, Pageable pageable) {
        if (genreIdsParam == null || genreIdsParam.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        boolean isAndSearch = genreIdsParam.contains(",");
        List<Long> genreIds = Arrays.stream(genreIdsParam.split("[,|]"))
                .map(genreId -> Long.parseLong(genreId)).collect(Collectors.toList());

        String sql = "SELECT b.* FROM books b " + "JOIN book_genres bg ON b.id = bg.book_id "
                + "WHERE bg.genre_id IN (:genreIds) ";

        if (isAndSearch) {
            sql += "GROUP BY b.id " + "HAVING COUNT(DISTINCT bg.genre_id) = :genreCount";
        } else {
            sql += "GROUP BY b.id";
        }

        sql += buildOrderByClause(pageable);

        Query query = entityManager.createNativeQuery(sql, Book.class);
        query.setParameter("genreIds", genreIds);
        if (isAndSearch) {
            query.setParameter("genreCount", genreIds.size());
        }

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        @SuppressWarnings("unchecked")
        List<Book> books = query.getResultList(); // ここで型の警告がでるので、アノテーション指定

        // トータル件数を取得するためのクエリ
        String countSql = "SELECT COUNT(DISTINCT b.id) FROM books b "
                + "JOIN book_genres bg ON b.id = bg.book_id " + "WHERE bg.genre_id IN (:genreIds) ";

        if (isAndSearch) {
            countSql += "GROUP BY b.id " + "HAVING COUNT(DISTINCT bg.genre_id) = :genreCount";
        } else {
            countSql += "GROUP BY b.id";
        }

        Query countQuery = entityManager.createNativeQuery(countSql);
        countQuery.setParameter("genreIds", genreIds);
        if (isAndSearch) {
            countQuery.setParameter("genreCount", genreIds.size());
        }

        long total = countQuery.getResultList().size();

        return new PageImpl<>(books, pageable, total);
    }

    // SortオブジェクトからORDER BY句を作成
    private String buildOrderByClause(Pageable pageable) {
        Sort sort = pageable.getSort().isSorted() ? pageable.getSort()
                : Sort.by(Sort.Direction.ASC, "title");
        StringBuilder orderBy = new StringBuilder(" ORDER BY ");
        sort.forEach(order -> {
            // エンティティのフィールド名をデータベースのカラム名に変換
            String columnName = convertCamelToSnake(order.getProperty());
            orderBy.append(columnName).append(" ").append(order.isAscending() ? "ASC" : "DESC")
                    .append(", ");
        });
        orderBy.setLength(orderBy.length() - 2); // 最後のカンマを削除
        return orderBy.toString();
    }

    private String convertCamelToSnake(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }

        StringBuilder snakeCase = new StringBuilder();
        char[] charArray = camelCase.toCharArray();

        for (char c : charArray) {
            if (Character.isUpperCase(c)) {
                snakeCase.append('_');
                snakeCase.append(Character.toLowerCase(c));
            } else {
                snakeCase.append(c);
            }
        }

        // 先頭にアンダースコアが付く場合があるので削除
        if (snakeCase.charAt(0) == '_') {
            snakeCase.deleteCharAt(0);
        }

        return snakeCase.toString();
    }
}
