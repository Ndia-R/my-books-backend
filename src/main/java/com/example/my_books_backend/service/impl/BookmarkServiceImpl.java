package com.example.my_books_backend.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import com.example.my_books_backend.util.PaginationUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookmarkServiceImpl implements BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final BookmarkMapper bookmarkMapper;

    private final BookRepository bookRepository;
    private final BookChapterRepository bookChapterRepository;
    private final PaginationUtil paginationUtil;

    private static final Sort DEFAULT_SORT =
            Sort.by(Sort.Order.desc("createdAt"), Sort.Order.asc("id"));

    @Override
    public List<BookmarkResponse> getBookmarksByBookId(String bookId, User user) {
        List<Bookmark> bookmarks =
                bookmarkRepository.findByBookIdAndUserAndIsDeletedFalse(bookId, user);
        List<BookmarkResponse> bookmarkResponses = bookmarkMapper.toBookmarkResponseList(bookmarks);

        // 書籍の目次のタイトルを取得し、章番号とタイトルのマップを作成する
        Map<Integer, String> chapterTitleMap = getChapterTitleMap(bookId);

        // 章番号に対応するタイトルをレスポンスに追加する
        bookmarkResponses.forEach(bookmarkResponse -> {
            String chapterTitle = chapterTitleMap.get(bookmarkResponse.getChapterNumber());
            if (chapterTitle != null) {
                bookmarkResponse.setChapterTitle(chapterTitle);
            }
        });

        return bookmarkResponses;
    }

    // getBookmarksByBookId()で使用するprivateメソッド
    private Map<Integer, String> getChapterTitleMap(String bookId) {
        List<BookChapter> bookChapters = bookChapterRepository.findByBookId(bookId);
        return bookChapters.stream().collect(Collectors.toMap(
                bookChapter -> bookChapter.getId().getChapterNumber(), BookChapter::getTitle));
    }

    @Override
    public BookmarkPageResponse getBookmarkPageByUser(Integer page, Integer maxResults, User user) {
        Pageable pageable = paginationUtil.createPageable(page, maxResults, DEFAULT_SORT);
        Page<Bookmark> bookmarkPage =
                bookmarkRepository.findByUserAndIsDeletedFalse(user, pageable);
        BookmarkPageResponse bookmarkPageResponse =
                bookmarkMapper.toBookmarkPageResponse(bookmarkPage);

        // 書籍の目次のタイトルを取得し、章番号とタイトルのマップを作成する
        Map<String, Map<Integer, String>> bookChapterTitleMaps =
                getBookChapterTitleMaps(bookmarkPage.getContent());

        // 章番号に対応するタイトルをレスポンスに追加する
        bookmarkPageResponse.getBookmarks().forEach(bookmarkResponse -> {
            Map<Integer, String> chapterTitleMap =
                    bookChapterTitleMaps.get(bookmarkResponse.getBook().getId());
            if (chapterTitleMap != null) {
                String chapterTitle = chapterTitleMap.get(bookmarkResponse.getChapterNumber());
                if (chapterTitle != null) {
                    bookmarkResponse.setChapterTitle(chapterTitle);
                }
            }
        });

        return bookmarkPageResponse;
    }

    // getBookmarkPageByUser()で使用するprivateメソッド
    private Map<String, Map<Integer, String>> getBookChapterTitleMaps(List<Bookmark> bookmarks) {
        Set<String> bookIds = bookmarks.stream().map(bookmark -> bookmark.getBook().getId())
                .collect(Collectors.toSet());

        Map<String, Map<Integer, String>> bookChapterTitleMaps = new HashMap<>();
        for (String bookId : bookIds) {
            List<BookChapter> bookChapters = bookChapterRepository.findByBookId(bookId);
            Map<Integer, String> chapterTitleMap = bookChapters.stream().collect(Collectors.toMap(
                    bookChapter -> bookChapter.getId().getChapterNumber(), BookChapter::getTitle));
            bookChapterTitleMaps.put(bookId, chapterTitleMap);
        }

        return bookChapterTitleMaps;
    }

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
