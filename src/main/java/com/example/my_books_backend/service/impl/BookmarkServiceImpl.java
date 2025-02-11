package com.example.my_books_backend.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.my_books_backend.dto.bookmark.BookmarkRequest;
import com.example.my_books_backend.dto.bookmark.BookmarkResponse;
import com.example.my_books_backend.dto.bookmark.BookmarkPageResponse;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.Bookmark;
import com.example.my_books_backend.entity.BookmarkId;
import com.example.my_books_backend.entity.User;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.mapper.BookmarkMapper;
import com.example.my_books_backend.repository.BookRepository;
import com.example.my_books_backend.repository.BookmarkRepository;
import com.example.my_books_backend.service.BookmarkService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final BookmarkMapper bookmarkMapper;

    private final BookRepository bookRepository;

    private static final Integer DEFAULT_START_PAGE = 0;
    private static final Integer DEFAULT_MAX_RESULTS = 20;
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "updatedAt");

    @Override
    public BookmarkResponse getBookmarkByBookId(String bookId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        BookmarkId bookmarkId = new BookmarkId(user.getId(), bookId);
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new NotFoundException("Bookmark not found"));
        return bookmarkMapper.toBookmarkResponse(bookmark);
    }

    @Override
    public BookmarkPageResponse getBookmarks(Integer page, Integer maxResults) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Pageable pageable = createPageable(page, maxResults);
        Page<Bookmark> bookmarkPage = bookmarkRepository.findByUserId(user.getId(), pageable);
        return bookmarkMapper.toBookmarkPageResponse(bookmarkPage);
    }

    @Override
    @Transactional
    public BookmarkResponse createBookmark(BookmarkRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new NotFoundException("Book not found"));
        BookmarkId bookmarkId = new BookmarkId(user.getId(), request.getBookId());
        Bookmark bookmark = new Bookmark();
        bookmark.setId(bookmarkId);
        bookmark.setUser(user);
        bookmark.setBook(book);
        bookmark.setPageNumber(request.getPageNumber());
        Bookmark savedBookmark = bookmarkRepository.save(bookmark);
        return bookmarkMapper.toBookmarkResponse(savedBookmark);
    }

    @Override
    @Transactional
    public BookmarkResponse updateBookmark(BookmarkRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        BookmarkId bookmarkId = new BookmarkId(user.getId(), request.getBookId());
        Bookmark bookmark = bookmarkRepository.findById(bookmarkId)
                .orElseThrow(() -> new NotFoundException("Bookmark not found"));

        Integer pageNumber = request.getPageNumber();

        if (pageNumber != null) {
            bookmark.setPageNumber(pageNumber);
        }

        Bookmark savedBookmark = bookmarkRepository.save(bookmark);
        return bookmarkMapper.toBookmarkResponse(savedBookmark);
    }

    @Override
    @Transactional
    public void deleteBookmark(String bookId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        BookmarkId bookmarkId = new BookmarkId(user.getId(), bookId);
        bookmarkRepository.deleteById(bookmarkId);
    }

    private Pageable createPageable(Integer page, Integer maxResults) {
        page = (page != null) ? page : DEFAULT_START_PAGE;
        maxResults = (maxResults != null) ? maxResults : DEFAULT_MAX_RESULTS;
        return PageRequest.of(page, maxResults, DEFAULT_SORT);
    }
}
