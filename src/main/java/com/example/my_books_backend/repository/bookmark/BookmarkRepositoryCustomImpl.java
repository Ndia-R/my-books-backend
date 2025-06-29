package com.example.my_books_backend.repository.bookmark;

import java.util.List;
import com.example.my_books_backend.entity.Bookmark;
import com.example.my_books_backend.entity.enums.SortableField.FieldCategory;
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
        String sortField,
        String sortDirection
    ) {
        Query query = CursorQueryBuilder
            .forEntity(Bookmark.class, entityManager)
            .filterByUser(userId)
            .withCursor(cursor)
            .withLimit(limit)
            .orderBy(sortField, sortDirection, FieldCategory.BOOKMARK)
            .build();

        @SuppressWarnings("unchecked")
        List<Bookmark> result = query.getResultList();
        return result;
    }
}
