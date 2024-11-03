package com.example.my_books_backend.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import com.example.my_books_backend.model.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    // // 基本的な呼び出し
    // List<String> genres = Arrays.asList("fiction", "mystery");
    // Pageable pageable = PageRequest.of(0, 10); // 1ページ目、10件ずつ
    // Page<Book> result = bookRepository.findByGenreIds(genres, pageable);

    // // ソート条件付きの呼び出し
    // Pageable pageableWithSort = PageRequest.of(
    // 0,
    // 10,
    // Sort.by(Sort.Direction.DESC, "publishedDate")
    // );
    // Page<Book> sortedResult = bookRepository.findByGenreIds(genres, pageableWithSort);

    @Override
    public Page<Book> findByGenreIds(List<String> genreIds, Pageable pageable) {
        // SQLクエリ作成
        String sql = buildQueryWithGenres("SELECT * FROM books WHERE ", genreIds);

        // ソート条件を適用
        sql += buildOrderByClause(pageable);

        // ページングクエリの実行
        Query query = entityManager.createNativeQuery(sql, Book.class);
        setParameters(query, genreIds);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        @SuppressWarnings("unchecked")
        List<Book> books = query.getResultList(); // ここで型の警告がでるので、アノテーション指定または型キャストする

        // 総件数取得用のクエリを作成
        String countSql = buildQueryWithGenres("SELECT COUNT(*) FROM books WHERE ", genreIds);
        Query countQuery = entityManager.createNativeQuery(countSql);
        setParameters(countQuery, genreIds);
        Long total = ((Number) countQuery.getSingleResult()).longValue();

        // 結果をPageオブジェクトで返却
        return new PageImpl<>(books, pageable, total);
    }

    /**
     * SQLクエリのWHERE句を、ジャンルIDのリストに基づいて動的に構築
     */
    private String buildQueryWithGenres(String baseQuery, List<String> genreIds) {
        StringBuilder query = new StringBuilder(baseQuery);
        for (int i = 0; i < genreIds.size(); i++) {
            if (i > 0) {
                query.append(" AND ");
            }
            query.append("FIND_IN_SET(:genre_id").append(i).append(", genre_ids) > 0");
        }
        return query.toString();
    }

    /**
     * SortオブジェクトからORDER BY句を作成
     */
    private String buildOrderByClause(Pageable pageable) {
        Sort sort = pageable.getSort().isSorted() ? pageable.getSort()
                : Sort.by(Sort.Direction.ASC, "title");
        StringBuilder orderBy = new StringBuilder(" ORDER BY ");
        sort.forEach(order -> {
            // エンティティのフィールド名をデータベースのカラム名に変換
            String columnName = convertToColumnName(order.getProperty());
            orderBy.append(columnName).append(" ").append(order.isAscending() ? "ASC" : "DESC")
                    .append(", ");
        });
        orderBy.setLength(orderBy.length() - 2); // 最後のカンマを削除
        return orderBy.toString();
    }

    /**
     * ジャンルIDのリストをクエリのパラメータに設定
     */
    private void setParameters(Query query, List<String> genreIds) {
        for (int i = 0; i < genreIds.size(); i++) {
            query.setParameter("genre_id" + i, genreIds.get(i));
        }
    }

    /**
     * エンティティのフィールド名をデータベースのカラム名に変換する
     */
    private String convertToColumnName(String fieldName) {
        switch (fieldName) {
            case "genreIds":
                return "genre_ids";
            case "publishedDate":
                return "published_date";
            case "pageCount":
                return "page_count";
            case "imageUrl":
                return "image_url";
            default:
                return fieldName;
        }
    }
}
