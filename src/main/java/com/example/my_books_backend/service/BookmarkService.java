package com.example.my_books_backend.service;

import com.example.my_books_backend.dto.bookmark.BookmarkPageResponse;
import com.example.my_books_backend.dto.bookmark.BookmarkRequest;
import com.example.my_books_backend.dto.bookmark.BookmarkResponse;
import com.example.my_books_backend.entity.User;
import java.util.List;

public interface BookmarkService {
    List<BookmarkResponse> getBookmarksByBookId(String bookId, User user);

    BookmarkPageResponse getBookmarkPageByUser(Integer page, Integer maxResults, User user);

    BookmarkResponse createBookmark(BookmarkRequest request, User user);

    BookmarkResponse updateBookmark(Long id, BookmarkRequest request, User user);

    void deleteBookmark(Long id, User user);
}
