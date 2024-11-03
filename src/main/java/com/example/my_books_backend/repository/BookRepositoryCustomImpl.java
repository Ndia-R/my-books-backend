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

    @Override
    public Page<Book> findByGenreIds(List<String> genreIds, Pageable pageable) {
        // 動的にFIND_IN_SETクエリを作成
        StringBuilder sql = new StringBuilder("SELECT * FROM books WHERE ");
        for (int i = 0; i < genreIds.size(); i++) {
            if (i > 0) {
                sql.append(" AND ");
            }
            sql.append("FIND_IN_SET(:genre_id").append(i).append(", genre_ids) > 0");
        }

        // ソート条件を追加
        Sort sort = pageable.getSort();
        if (sort.isSorted()) {
            sql.append(" ORDER BY ");
            sort.forEach(order -> {
                sql.append(order.getProperty()).append(" ")
                        .append(order.isAscending() ? "ASC" : "DESC").append(", ");
            });
            // 最後のカンマを削除
            sql.setLength(sql.length() - 2);
        }

        // クエリを作成
        Query query = entityManager.createNativeQuery(sql.toString(), Book.class);

        // パラメータを設定
        for (int i = 0; i < genreIds.size(); i++) {
            query.setParameter("genre_id" + i, genreIds.get(i));
        }

        // ページネーションの設定: 開始位置と最大件数を設定
        query.setFirstResult((int) pageable.getOffset()); // 何件目から開始するか
        query.setMaxResults(pageable.getPageSize()); // 1ページあたりの最大件数

        // 結果リストを取得
        @SuppressWarnings("unchecked")
        List<Book> books = query.getResultList(); // ここで型の警告がでるので、アノテーションか型キャストする

        // 総件数を取得するクエリ
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM books WHERE ");
        for (int i = 0; i < genreIds.size(); i++) {
            if (i > 0) {
                countSql.append(" AND ");
            }
            countSql.append("FIND_IN_SET(:genre_id").append(i).append(", genre_ids) > 0");
        }

        // クエリを作成
        Query countQuery = entityManager.createNativeQuery(countSql.toString());

        // パラメータを設定
        for (int i = 0; i < genreIds.size(); i++) {
            countQuery.setParameter("genre_id" + i, genreIds.get(i));
        }

        // 総件数を取得
        Long total = ((Number) countQuery.getSingleResult()).longValue();

        // Pageオブジェクトを作成して返す
        return new PageImpl<>(books, pageable, total);
    }
}
