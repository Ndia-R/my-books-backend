package com.example.my_books_backend.repository.book;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.util.StringCaseUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

public class BookRepositoryCustomImpl implements BookRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Book> findBooksByTitleKeywordWithCursor(String keyword, String cursor, int limit,
            String sortField, String sortDirection) {

        String columnName = StringCaseUtils.camelToSnake(sortField);
        String comparison = "asc".equalsIgnoreCase(sortDirection) ? ">" : "<";
        String orderDirection = "asc".equalsIgnoreCase(sortDirection) ? "ASC" : "DESC";

        String sql = String.format("""
                SELECT * FROM books b
                WHERE (:cursor IS NULL OR
                    (b.%s %s (SELECT b2.%s FROM books b2 WHERE b2.id = :cursor) OR
                    (b.%s = (SELECT b2.%s FROM books b2 WHERE b2.id = :cursor) AND b.id > :cursor)))
                AND b.title LIKE :keyword
                AND b.is_deleted = false
                ORDER BY
                    b.%s %s,
                    b.id ASC
                LIMIT :limit
                """, columnName, comparison, columnName, columnName, columnName, columnName,
                orderDirection);

        Query query = entityManager.createNativeQuery(sql, Book.class);
        query.setParameter("keyword", "%" + keyword + "%");
        query.setParameter("cursor", cursor);
        query.setParameter("limit", limit);

        @SuppressWarnings("unchecked")
        List<Book> result = query.getResultList();
        return result;
    }

    @Override
    public List<Book> findBooksByGenresOrWithCursor(List<Long> genreIds, String cursor, int limit,
            String sortField, String sortDirection) {

        if (genreIds == null || genreIds.isEmpty()) {
            return List.of();
        }

        String columnName = StringCaseUtils.camelToSnake(sortField);
        String comparison = "asc".equalsIgnoreCase(sortDirection) ? ">" : "<";
        String orderDirection = "asc".equalsIgnoreCase(sortDirection) ? "ASC" : "DESC";

        // named parameterでIN句を動的生成
        String genreIdPlaceholders = IntStream.range(0, genreIds.size())
                .mapToObj(i -> ":genreId" + i).collect(Collectors.joining(","));

        String sql = String.format("""
                SELECT DISTINCT b.* FROM books b
                JOIN book_genres bg ON b.id = bg.book_id
                JOIN genres g ON bg.genre_id = g.id
                WHERE (:cursor IS NULL OR
                    (b.%s %s (SELECT b2.%s FROM books b2 WHERE b2.id = :cursor) OR
                    (b.%s = (SELECT b2.%s FROM books b2 WHERE b2.id = :cursor) AND b.id > :cursor)))
                AND g.id IN (%s)
                AND b.is_deleted = false
                ORDER BY
                    b.%s %s,
                    b.id ASC
                LIMIT :limit
                """, columnName, comparison, columnName, columnName, columnName,
                genreIdPlaceholders, columnName, orderDirection);

        Query query = entityManager.createNativeQuery(sql, Book.class);
        query.setParameter("cursor", cursor);
        query.setParameter("limit", limit);

        // named parameterでジャンルIDを設定
        for (int i = 0; i < genreIds.size(); i++) {
            query.setParameter("genreId" + i, genreIds.get(i));
        }

        @SuppressWarnings("unchecked")
        List<Book> result = query.getResultList();
        return result;
    }

    @Override
    public List<Book> findBooksByGenresAndWithCursor(List<Long> genreIds, String cursor, int limit,
            String sortField, String sortDirection) {

        if (genreIds == null || genreIds.isEmpty()) {
            return List.of();
        }

        String columnName = StringCaseUtils.camelToSnake(sortField);
        String comparison = "asc".equalsIgnoreCase(sortDirection) ? ">" : "<";
        String orderDirection = "asc".equalsIgnoreCase(sortDirection) ? "ASC" : "DESC";

        // named parameterでIN句を動的生成
        String genreIdPlaceholders = IntStream.range(0, genreIds.size())
                .mapToObj(i -> ":genreId" + i).collect(Collectors.joining(","));

        String sql = String.format("""
                SELECT b.* FROM books b
                JOIN book_genres bg ON b.id = bg.book_id
                JOIN genres g ON bg.genre_id = g.id
                WHERE (:cursor IS NULL OR
                    (b.%s %s (SELECT b2.%s FROM books b2 WHERE b2.id = :cursor) OR
                    (b.%s = (SELECT b2.%s FROM books b2 WHERE b2.id = :cursor) AND b.id > :cursor)))
                AND g.id IN (%s)
                AND b.is_deleted = false
                GROUP BY b.id, b.%s
                HAVING COUNT(DISTINCT g.id) = :genreCount
                ORDER BY
                    b.%s %s,
                    b.id ASC
                LIMIT :limit
                """, columnName, comparison, columnName, columnName, columnName,
                genreIdPlaceholders, columnName, columnName, orderDirection);

        Query query = entityManager.createNativeQuery(sql, Book.class);
        query.setParameter("cursor", cursor);
        query.setParameter("limit", limit);
        query.setParameter("genreCount", genreIds.size());

        // named parameterでジャンルIDを設定
        for (int i = 0; i < genreIds.size(); i++) {
            query.setParameter("genreId" + i, genreIds.get(i));
        }

        @SuppressWarnings("unchecked")
        List<Book> result = query.getResultList();
        return result;
    }
}
