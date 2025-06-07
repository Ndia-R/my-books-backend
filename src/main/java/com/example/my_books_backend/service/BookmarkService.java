package com.example.my_books_backend.service;

import com.example.my_books_backend.dto.CursorPageResponse;
import com.example.my_books_backend.dto.PageResponse;
import com.example.my_books_backend.dto.bookmark.BookmarkRequest;
import com.example.my_books_backend.dto.bookmark.BookmarkResponse;
import com.example.my_books_backend.entity.User;
import org.springframework.data.domain.Pageable;

public interface BookmarkService {
    /**
     * ユーザーが追加したブックマークを取得
     * 
     * @param user ユーザーエンティティ
     * @param pageable ページネーション情報（ページ番号、ページサイズ、ソート条件）
     * @param bookId 書籍ID、nullの場合はすべてが対象
     * @return ブックマークリスト
     */
    PageResponse<BookmarkResponse> getUserBookmarks(User user, Pageable pageable, String bookId);

    /**
     * ユーザーが追加したブックマークを取得（カーソルベース）
     * 
     * @param user ユーザーエンティティ
     * @param cursor カーソルID、nullの場合は先頭からlimit分のデータが返却される
     * @param limit 1ページあたりの最大結果件数、nullの場合はデフォルト値が使用される
     * @return ブックマークリスト
     */
    CursorPageResponse<BookmarkResponse> getUserBookmarksWithCursor(User user, String cursor,
            Integer limit);

    /**
     * ブックマークを作成
     * 
     * @param request ブックマーク作成リクエスト
     * @param user ユーザーエンティティ
     * @return 作成されたブックマーク情報
     */
    BookmarkResponse createBookmark(BookmarkRequest request, User user);

    /**
     * ブックマークを更新
     * 
     * @param id 更新するブックマークのID
     * @param request ブックマーク更新リクエスト
     * @param user ユーザーエンティティ
     * @return 更新されたブックマーク情報
     */
    BookmarkResponse updateBookmark(Long id, BookmarkRequest request, User user);

    /**
     * ブックマークを削除
     * 
     * @param id 削除するブックマークのID
     * @param user ユーザーエンティティ
     */
    void deleteBookmark(Long id, User user);
}
