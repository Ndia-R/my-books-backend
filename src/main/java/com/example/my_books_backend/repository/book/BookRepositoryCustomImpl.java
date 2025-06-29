package com.example.my_books_backend.repository.book;

import java.util.List;
import com.example.my_books_backend.entity.Book;
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
        String sortString
    ) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }

        Query query = CursorQueryBuilder
            .of(Book.class, entityManager)
            .filterByTitleKeyword(keyword)
            .withCursor(cursor)
            .withLimit(limit)
            .orderBy(sortString)
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
        String sortString
    ) {
        if (genreIds == null || genreIds.isEmpty()) {
            return List.of();
        }

        // OR条件
        Query query = CursorQueryBuilder
            .of(Book.class, entityManager)
            .filterByGenresOr(genreIds)
            .withCursor(cursor)
            .withLimit(limit)
            .orderBy(sortString)
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
        String sortString
    ) {
        if (genreIds == null || genreIds.isEmpty()) {
            return List.of();
        }

        // AND条件
        Query query = CursorQueryBuilder
            .of(Book.class, entityManager)
            .filterByGenresAnd(genreIds)
            .withCursor(cursor)
            .withLimit(limit)
            .orderBy(sortString)
            .build();

        @SuppressWarnings("unchecked")
        List<Book> result = query.getResultList();
        return result;
    }
}
