package com.example.my_books_backend.service;

import com.example.my_books_backend.dto.bookmark.BookmarkRequest;
import com.example.my_books_backend.dto.bookmark.BookmarkResponse;
import com.example.my_books_backend.dto.bookmark.BookmarkPageResponse;

public interface BookmarkService {
    BookmarkResponse getBookmarkById(String bookId);

    BookmarkPageResponse getBookmarkPage(Integer page, Integer maxResults);

    BookmarkResponse createBookmark(BookmarkRequest request);

    BookmarkResponse updateBookmark(BookmarkRequest request);

    void deleteBookmark(String bookId);
}
