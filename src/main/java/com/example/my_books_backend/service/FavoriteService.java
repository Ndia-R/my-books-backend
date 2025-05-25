package com.example.my_books_backend.service;

import com.example.my_books_backend.dto.favorite.FavoriteRequest;
import com.example.my_books_backend.dto.favorite.FavoriteResponse;
import com.example.my_books_backend.entity.User;
import org.springframework.data.domain.Pageable;
import com.example.my_books_backend.dto.favorite.FavoriteCountsResponse;
import com.example.my_books_backend.dto.favorite.FavoriteCursorResponse;
import com.example.my_books_backend.dto.favorite.FavoritePageResponse;

public interface FavoriteService {
    /**
     * ユーザーが追加したお気に入りを取得
     * 
     * @param user ユーザーエンティティ
     * @param pageable ページネーション情報（ページ番号、ページサイズ、ソート条件）
     * @param bookId 書籍ID、nullの場合はすべてが対象
     * @return お気に入りリスト
     */
    FavoritePageResponse getUserFavorites(User user, Pageable pageable, String bookId);

    /**
     * ユーザーが追加したお気に入りを取得（カーソルベース）
     * 
     * @param user ユーザーエンティティ
     * @param cursor カーソルID、nullの場合は先頭からlimit分のデータが返却される
     * @param limit 1ページあたりの最大結果件数、nullの場合はデフォルト値が使用される
     * @return お気に入りリスト
     */
    FavoriteCursorResponse getUserFavoritesWithCursor(User user, Long cursor, Integer limit);

    /**
     * 書籍に対するお気に入り数を取得
     * 
     * @param bookId 書籍ID
     * @return お気に入り数
     */
    FavoriteCountsResponse getBookFavoriteCounts(String bookId);

    /**
     * お気に入りを作成
     * 
     * @param request お気に入り作成リクエスト
     * @param user ユーザーエンティティ
     * @return 作成されたお気に入り情報
     */
    FavoriteResponse createFavorite(FavoriteRequest request, User user);

    /**
     * お気に入りを削除
     * 
     * @param id 削除するお気に入りのID
     * @param user ユーザーエンティティ
     */
    void deleteFavorite(Long id, User user);
}
