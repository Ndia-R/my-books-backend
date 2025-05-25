package com.example.my_books_backend.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.my_books_backend.dto.bookmark.BookmarkCursorResponse;
import com.example.my_books_backend.dto.bookmark.BookmarkPageResponse;
import com.example.my_books_backend.dto.bookmark.BookmarkRequest;
import com.example.my_books_backend.dto.bookmark.BookmarkResponse;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.BookChapter;
import com.example.my_books_backend.entity.Bookmark;
import com.example.my_books_backend.entity.User;
import com.example.my_books_backend.exception.ConflictException;
import com.example.my_books_backend.exception.ForbiddenException;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.mapper.BookmarkMapper;
import com.example.my_books_backend.repository.BookChapterRepository;
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
    private final BookChapterRepository bookChapterRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public BookmarkPageResponse getUserBookmarks(User user, Pageable pageable, String bookId) {
        Page<Bookmark> bookmarkPage = (bookId == null)
                ? bookmarkRepository.findByUserAndIsDeletedFalse(user, pageable)
                : bookmarkRepository.findByUserAndIsDeletedFalseAndBookId(user, pageable, bookId);
        BookmarkPageResponse response = bookmarkMapper.toBookmarkPageResponse(bookmarkPage);

        // 書籍の目次のタイトルを取得し、章番号とタイトルのマップを作成する
        Set<String> bookIds = bookmarkPage.getContent().stream()
                .map(bookmark -> bookmark.getBook().getId()).collect(Collectors.toSet());

        Map<String, Map<Integer, String>> bookChapterTitleMaps = new HashMap<>();
        for (String _bookId : bookIds) {
            List<BookChapter> bookChapters = bookChapterRepository.findByBookId(_bookId);
            Map<Integer, String> chapterTitleMap = bookChapters.stream().collect(Collectors.toMap(
                    bookChapter -> bookChapter.getId().getChapterNumber(), BookChapter::getTitle));
            bookChapterTitleMaps.put(_bookId, chapterTitleMap);
        }

        // 章番号に対応するタイトルをレスポンスに追加する
        response.getBookmarks().forEach(bookmark -> {
            Map<Integer, String> chapterTitleMap =
                    bookChapterTitleMaps.get(bookmark.getBook().getId());
            if (chapterTitleMap != null) {
                String chapterTitle = chapterTitleMap.get(bookmark.getChapterNumber());
                if (chapterTitle != null) {
                    bookmark.setChapterTitle(chapterTitle);
                }
            }
        });

        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BookmarkCursorResponse getUserBookmarksWithCursor(User user, Long cursor,
            Integer limit) {
        // 次のページの有無を判定するために、1件多く取得
        List<Bookmark> bookmarks =
                bookmarkRepository.findBookmarksByUserIdWithCursor(user.getId(), cursor, limit + 1);
        BookmarkCursorResponse response = bookmarkMapper.toBookmarkCursorResponse(bookmarks, limit);

        // 書籍の目次のタイトルを取得し、章番号とタイトルのマップを作成する
        Set<String> bookIds = bookmarks.stream().map(bookmark -> bookmark.getBook().getId())
                .collect(Collectors.toSet());

        Map<String, Map<Integer, String>> bookChapterTitleMaps = new HashMap<>();
        for (String bookId : bookIds) {
            List<BookChapter> bookChapters = bookChapterRepository.findByBookId(bookId);
            Map<Integer, String> chapterTitleMap = bookChapters.stream().collect(Collectors.toMap(
                    bookChapter -> bookChapter.getId().getChapterNumber(), BookChapter::getTitle));
            bookChapterTitleMaps.put(bookId, chapterTitleMap);
        }

        // 章番号に対応するタイトルをレスポンスに追加する
        response.getBookmarks().forEach(bookmark -> {
            Map<Integer, String> chapterTitleMap =
                    bookChapterTitleMaps.get(bookmark.getBook().getId());
            if (chapterTitleMap != null) {
                String chapterTitle = chapterTitleMap.get(bookmark.getChapterNumber());
                if (chapterTitle != null) {
                    bookmark.setChapterTitle(chapterTitle);
                }
            }
        });

        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public BookmarkResponse createBookmark(BookmarkRequest request, User user) {
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new NotFoundException("Book not found"));

        Optional<Bookmark> existingBookmark =
                bookmarkRepository.findByUserAndBookAndChapterNumberAndPageNumber(user, book,
                        request.getChapterNumber(), request.getPageNumber());

        Bookmark bookmark = new Bookmark();
        if (existingBookmark.isPresent()) {
            bookmark = existingBookmark.get();
            if (bookmark.getIsDeleted()) {
                bookmark.setIsDeleted(false);
            } else {
                throw new ConflictException("すでにこのページにはブックマークが登録されています。");
            }
        }
        bookmark.setUser(user);
        bookmark.setBook(book);
        bookmark.setChapterNumber(request.getChapterNumber());
        bookmark.setPageNumber(request.getPageNumber());
        bookmark.setNote(request.getNote());

        Bookmark savedBookmark = bookmarkRepository.save(bookmark);
        return bookmarkMapper.toBookmarkResponse(savedBookmark);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public BookmarkResponse updateBookmark(Long id, BookmarkRequest request, User user) {
        Bookmark bookmark = bookmarkRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bookmark not found"));

        if (!bookmark.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("このブックマークを編集する権限がありません。");
        }

        String note = request.getNote();

        if (note != null) {
            bookmark.setNote(note);
        }

        Bookmark savedBookmark = bookmarkRepository.save(bookmark);
        return bookmarkMapper.toBookmarkResponse(savedBookmark);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteBookmark(Long id, User user) {
        Bookmark bookmark = bookmarkRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Bookmark not found"));

        if (!bookmark.getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("このブックマークを削除する権限がありません");
        }

        bookmark.setIsDeleted(true);
        bookmarkRepository.save(bookmark);
    }
}
