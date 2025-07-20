package com.example.my_books_backend.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import com.example.my_books_backend.dto.bookmark.BookmarkResponse;
import com.example.my_books_backend.dto.PageResponse;
import com.example.my_books_backend.entity.Bookmark;
import com.example.my_books_backend.util.PageableUtils;

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
        return PageableUtils.toPageResponse(bookmarks, responses);
    }
}
