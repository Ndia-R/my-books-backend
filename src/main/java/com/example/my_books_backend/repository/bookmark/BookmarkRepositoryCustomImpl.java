package com.example.my_books_backend.repository.bookmark;

import java.util.List;
import com.example.my_books_backend.entity.Bookmark;
import com.example.my_books_backend.util.CursorQueryBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

public class BookmarkRepositoryCustomImpl implements BookmarkRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Bookmark> findBookmarksByUserIdWithCursor(
        Long userId,
        Long cursor,
        int limit,
        String sortString
    ) {
        Query query = CursorQueryBuilder
            .of(Bookmark.class, entityManager)
            .filterByUser(userId)
            .withCursor(cursor)
            .withLimit(limit)
            .orderBy(sortString)
            .build();

        @SuppressWarnings("unchecked")
        List<Bookmark> result = query.getResultList();
        return result;
    }
}
