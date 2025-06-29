package com.example.my_books_backend.repository.favorite;

import java.util.List;
import com.example.my_books_backend.entity.Favorite;
import com.example.my_books_backend.entity.enums.SortableField.FieldCategory;
import com.example.my_books_backend.util.CursorQueryBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

public class FavoriteRepositoryCustomImpl implements FavoriteRepositoryCustom {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Favorite> findFavoritesByUserIdWithCursor(
        Long userId,
        Long cursor,
        int limit,
        String sortField,
        String sortDirection
    ) {
        Query query = CursorQueryBuilder
            .forEntity(Favorite.class, entityManager)
            .filterByUser(userId)
            .withCursor(cursor)
            .withLimit(limit)
            .orderBy(sortField, sortDirection, FieldCategory.FAVORITE)
            .build();

        @SuppressWarnings("unchecked")
        List<Favorite> result = query.getResultList();
        return result;
    }
}
