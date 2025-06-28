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

        // ✅ 1段階の動的クエリ生成（Fluent Builder Pattern）
        Query query = CursorQueryBuilder.forEntity(Bookmark.class, entityManager)
            .fromBookmarks()
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
