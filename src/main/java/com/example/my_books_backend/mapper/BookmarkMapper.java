package com.example.my_books_backend.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import com.example.my_books_backend.dto.bookmark.BookmarkResponse;
import com.example.my_books_backend.dto.CursorPageResponse;
import com.example.my_books_backend.dto.PageResponse;
import com.example.my_books_backend.entity.Bookmark;

@Mapper(componentModel = "spring")
public abstract class BookmarkMapper {

    @Autowired
    protected BookMapper bookMapper;

    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "book", expression = "java(bookMapper.toBookResponse(bookmark.getBook()))")
    @Mapping(target = "chapterTitle", ignore = true) // サービスレイヤーで設定されるため無視
    public abstract BookmarkResponse toBookmarkResponse(Bookmark bookmark);

    public abstract List<BookmarkResponse> toBookmarkResponseList(List<Bookmark> bookmarks);

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
