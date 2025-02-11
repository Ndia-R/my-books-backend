package com.example.my_books_backend.mapper;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.dto.bookmark.BookmarkResponse;
import com.example.my_books_backend.dto.bookmark.BookmarkPageResponse;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.Bookmark;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookmarkMapper {
    private final ModelMapper modelMapper;
    private final BookMapper bookMapper;

    public BookmarkResponse toBookmarkResponse(Bookmark bookmark) {
        BookmarkResponse bookmarkResponse = modelMapper.map(bookmark, BookmarkResponse.class);
        Book book = modelMapper.map(bookmark.getBook(), Book.class);
        bookmarkResponse.setBook(bookMapper.toBookResponse(book));
        return bookmarkResponse;
    }

    public List<BookmarkResponse> toBookmarkResponseList(List<Bookmark> bookmarks) {
        return bookmarks.stream().map(bookmark -> toBookmarkResponse(bookmark)).toList();
    }

    public BookmarkPageResponse toBookmarkPageResponse(Page<Bookmark> bookmarkPage) {
        Integer page = bookmarkPage.getNumber();
        Integer totalPages = bookmarkPage.getTotalPages();
        Integer totalItems = (int) bookmarkPage.getTotalElements();
        List<BookmarkResponse> bookmarkResponses =
                toBookmarkResponseList(bookmarkPage.getContent());
        return new BookmarkPageResponse(page, totalPages, totalItems, bookmarkResponses);
    }
}
