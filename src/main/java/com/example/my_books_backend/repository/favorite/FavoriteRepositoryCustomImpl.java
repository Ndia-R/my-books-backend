package com.example.my_books_backend.repository.favorite;

import java.util.List;
import com.example.my_books_backend.entity.Favorite;
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
        String sortString
    ) {
        Query query = CursorQueryBuilder
            .of(Favorite.class, entityManager)
            .filterByUser(userId)
            .withCursor(cursor)
            .withLimit(limit)
            .orderBy(sortString)
            .build();

        @SuppressWarnings("unchecked")
        List<Favorite> result = query.getResultList();
        return result;
    }
}
