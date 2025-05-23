package com.example.my_books_backend.service;

import com.example.my_books_backend.dto.favorite.FavoriteRequest;
import com.example.my_books_backend.dto.favorite.FavoriteResponse;
import com.example.my_books_backend.entity.User;
import java.util.List;
import com.example.my_books_backend.dto.favorite.FavoriteCountsResponse;
import com.example.my_books_backend.dto.favorite.FavoritePageResponse;

public interface FavoriteService {
    /**
     * ユーザーが追加した特定の書籍のお気に入りを取得
     * 
     * @param bookId 書籍ID
     * @param user ユーザーエンティティ
     * @return お気に入り情報
     */
    FavoriteResponse getUserFavoriteForBook(String bookId, User user);

    /**
     * ユーザーが追加したすべてのお気に入りを取得（ページング形式）
     * 
     * @param page ページ番号（0ベース）、nullの場合はデフォルト値が使用される
     * @param maxResults 1ページあたりの最大結果件数、nullの場合はデフォルト値が使用される
     * @param user ユーザーエンティティ
     * @return お気に入りリスト
     */
    FavoritePageResponse getUserFavorites(Integer page, Integer maxResults, User user);

    /**
     * ユーザーが追加したすべてのお気に入りを取得（カーソル方式で取得）
     * 
     * @param cursorId カーソルID（レビューID）、nullの場合は先頭からmaxResults分のデータが返却される
     * @param maxResults 1ページあたりの最大結果件数、nullの場合はデフォルト値が使用される
     * @return お気に入りリスト
     */
    List<FavoriteResponse> getUserFavoritesByCursor(Long cursorId, Integer maxResults, User user);

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
    void deleteFavorite(String bookId, User user);
}
