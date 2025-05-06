package com.example.my_books_backend.service;

import com.example.my_books_backend.dto.bookmark.BookmarkPageResponse;
import com.example.my_books_backend.dto.bookmark.BookmarkRequest;
import com.example.my_books_backend.dto.bookmark.BookmarkResponse;
import com.example.my_books_backend.entity.User;
import java.util.List;

public interface BookmarkService {
    /**
     * ユーザーが追加した特定の書籍のブックマークリストを取得
     * 
     * @param bookId 書籍ID
     * @param user ユーザーエンティティ
     * @return ブックマークリスト
     */
    List<BookmarkResponse> getUserBookmarksForBook(String bookId, User user);

    /**
     * ユーザーが追加したすべてのブックマークを取得（ページング形式）
     * 
     * @param page ページ番号（0ベース）、nullの場合はデフォルト値が使用される
     * @param maxResults 1ページあたりの最大結果件数、nullの場合はデフォルト値が使用される
     * @param user ユーザーエンティティ
     * @return ブックマークリスト
     */
    BookmarkPageResponse getUserBookmarks(Integer page, Integer maxResults, User user);

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
