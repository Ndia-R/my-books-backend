package com.example.my_books_backend.repository.favorite;

import java.util.List;
import com.example.my_books_backend.entity.Favorite;

public interface FavoriteRepositoryCustom {
    // ユーザーが追加したお気に入りを取得（カーソルベース）
    List<Favorite> findFavoritesByUserIdWithCursor(
        Long userId,
        Long cursor,
        int limit,
        String sortString
    );
}
