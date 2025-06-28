package com.example.my_books_backend.repository.bookmark;

import java.util.List;
import com.example.my_books_backend.entity.Bookmark;

public interface BookmarkRepositoryCustom {
    // ユーザーが追加したブックマークを取得（カーソルベース）
    List<Bookmark> findBookmarksByUserIdWithCursor(
        Long userId,
        Long cursor,
        int limit,
        String sortField,
        String sortDirection
    );
}
