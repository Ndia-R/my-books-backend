package com.example.my_books_backend.mapper;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.dto.bookmark.BookmarkResponse;
import com.example.my_books_backend.dto.CursorPageResponse;
import com.example.my_books_backend.dto.PageResponse;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.Bookmark;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookmarkMapper {
    private final ModelMapper modelMapper;
    private final BookMapper bookMapper;

    public BookmarkResponse toBookmarkResponse(Bookmark bookmark) {
        BookmarkResponse response = modelMapper.map(bookmark, BookmarkResponse.class);
        Book book = modelMapper.map(bookmark.getBook(), Book.class);
        response.setBook(bookMapper.toBookResponse(book));
        return response;
    }

    public List<BookmarkResponse> toBookmarkResponseList(List<Bookmark> bookmarks) {
        return bookmarks.stream().map(bookmark -> toBookmarkResponse(bookmark)).toList();
    }

    public PageResponse<BookmarkResponse> toPageResponse(Page<Bookmark> bookmarks) {
        List<BookmarkResponse> responses = toBookmarkResponseList(bookmarks.getContent());
        // Pageableの内部的にはデフォルトで0ベースだが、エンドポイントとしては1ベースなので+1する
        return new PageResponse<BookmarkResponse>(bookmarks.getNumber() + 1, bookmarks.getSize(),
                bookmarks.getTotalPages(), bookmarks.getTotalElements(), bookmarks.hasNext(),
                bookmarks.hasPrevious(), responses);
    }

    public CursorPageResponse<BookmarkResponse> toCursorPageResponse(List<Bookmark> bookmarks,
            Integer limit) {
        Boolean hasNext = bookmarks.size() > limit;
        if (hasNext) {
            bookmarks = bookmarks.subList(0, limit); // 余分な1件を削除
        }
        String endCursor = hasNext ? bookmarks.get(bookmarks.size() - 1).getId().toString() : null;
        List<BookmarkResponse> responses = toBookmarkResponseList(bookmarks);
        return new CursorPageResponse<BookmarkResponse>(endCursor, hasNext, responses);
    }
}
