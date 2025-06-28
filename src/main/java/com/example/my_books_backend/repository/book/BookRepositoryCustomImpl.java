package com.example.my_books_backend.repository.book;

import java.util.List;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.enums.SortableField.FieldCategory;
import com.example.my_books_backend.util.CursorQueryBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

public class BookRepositoryCustomImpl implements BookRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Book> findBooksByTitleKeywordWithCursor(
        String keyword,
        String cursor,
        int limit,
        String sortField,
        String sortDirection
    ) {

        // ✅ 1段階の動的クエリ生成（Fluent Builder Pattern）
        Query query = CursorQueryBuilder.forEntity(Book.class, entityManager)
            .fromBooks()
            .filterByTitleKeyword(keyword)
            .withCursor(cursor)
            .withLimit(limit)
            .orderBy(sortField, sortDirection, FieldCategory.BOOK)
            .build();

        @SuppressWarnings("unchecked")
        List<Book> result = query.getResultList();
        return result;
    }

    @Override
    public List<Book> findBooksByGenresOrWithCursor(
        List<Long> genreIds,
        String cursor,
        int limit,
        String sortField,
        String sortDirection
    ) {

        if (genreIds == null || genreIds.isEmpty()) {
            return List.of();
        }

        // ✅ 1段階の動的クエリ生成（OR条件）
        Query query = CursorQueryBuilder.forEntity(Book.class, entityManager)
            .fromBooks()
            .filterByGenresOr(genreIds)
            .withCursor(cursor)
            .withLimit(limit)
            .orderBy(sortField, sortDirection, FieldCategory.BOOK)
            .build();

        @SuppressWarnings("unchecked")
        List<Book> result = query.getResultList();
        return result;
    }

    @Override
    public List<Book> findBooksByGenresAndWithCursor(
        List<Long> genreIds,
        String cursor,
        int limit,
        String sortField,
        String sortDirection
    ) {

        if (genreIds == null || genreIds.isEmpty()) {
            return List.of();
        }

        // ✅ 1段階の動的クエリ生成（AND条件）
        Query query = CursorQueryBuilder.forEntity(Book.class, entityManager)
            .fromBooks()
            .filterByGenresAnd(genreIds)
            .withCursor(cursor)
            .withLimit(limit)
            .orderBy(sortField, sortDirection, FieldCategory.BOOK)
            .build();

        @SuppressWarnings("unchecked")
        List<Book> result = query.getResultList();
        return result;
    }
}
