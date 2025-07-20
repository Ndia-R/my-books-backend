package com.example.my_books_backend.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.my_books_backend.dto.genre.GenreResponse;
import com.example.my_books_backend.dto.PageResponse;
import com.example.my_books_backend.dto.book.BookDetailsResponse;
import com.example.my_books_backend.dto.book.BookResponse;
import com.example.my_books_backend.dto.book_chapter.BookChapterResponse;
import com.example.my_books_backend.dto.book_chapter.BookTableOfContentsResponse;
import com.example.my_books_backend.dto.book_chapter_page_content.BookChapterPageContentResponse;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.BookChapter;
import com.example.my_books_backend.entity.BookChapterId;
import com.example.my_books_backend.entity.BookChapterPageContent;
import com.example.my_books_backend.entity.BookChapterPageContentId;
import com.example.my_books_backend.entity.Genre;
import com.example.my_books_backend.exception.BadRequestException;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.mapper.BookMapper;
import com.example.my_books_backend.repository.BookChapterRepository;
import com.example.my_books_backend.repository.BookRepository;
import com.example.my_books_backend.repository.BookChapterPageContentRepository;
import com.example.my_books_backend.service.BookService;
import com.example.my_books_backend.service.GenreService;
import com.example.my_books_backend.util.PageableUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookChapterRepository bookChapterRepository;
    private final BookChapterPageContentRepository bookChapterPageContentRepository;
    private final BookMapper bookMapper;

    private final GenreService genreService;

    /**
     * {@inheritDoc}
     */
    @Override
    public PageResponse<BookResponse> getBooks(
        Integer page,
        Integer size,
        String sortString
    ) {
        Pageable pageable = PageableUtils.createPageable(
            page,
            size,
            sortString,
            PageableUtils.BOOK_ALLOWED_FIELDS
        );
        Page<Book> pageObj = bookRepository.findByIsDeletedFalse(pageable);

        // 2クエリ戦略：IDリストから関連データを含むリストを取得
        List<String> ids = pageObj.getContent().stream().map(Book::getId).toList();
        List<Book> list = bookRepository.findAllByIdInWithRelations(ids);

        // ソート順序を復元
        List<Book> sortedList = PageableUtils.restoreSortOrder(ids, list, Book::getId);

        // 元のページネーション情報を保持して新しいPageオブジェクトを作成
        Page<Book> updatedPageObj = new PageImpl<>(
            sortedList,
            pageable,
            pageObj.getTotalElements()
        );

        return bookMapper.toPageResponse(updatedPageObj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageResponse<BookResponse> getBooksByTitleKeyword(
        String keyword,
        Integer page,
        Integer size,
        String sortString
    ) {
        Pageable pageable = PageableUtils.createPageable(
            page,
            size,
            sortString,
            PageableUtils.BOOK_ALLOWED_FIELDS
        );
        Page<Book> pageObj = bookRepository.findByTitleContainingAndIsDeletedFalse(keyword, pageable);

        // 2クエリ戦略：IDリストから関連データを含むリストを取得
        List<String> ids = pageObj.getContent().stream().map(Book::getId).toList();
        List<Book> list = bookRepository.findAllByIdInWithRelations(ids);

        // ソート順序を復元
        List<Book> sortedList = PageableUtils.restoreSortOrder(ids, list, Book::getId);

        // 元のページネーション情報を保持して新しいPageオブジェクトを作成
        Page<Book> updatedPageObj = new PageImpl<>(
            sortedList,
            pageable,
            pageObj.getTotalElements()
        );

        return bookMapper.toPageResponse(updatedPageObj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageResponse<BookResponse> getBooksByGenre(
        String genreIdsQuery,
        String conditionQuery,
        Integer page,
        Integer size,
        String sortString
    ) {
        if (!("SINGLE".equals(conditionQuery)
            || "AND".equals(conditionQuery)
            || "OR".equals(conditionQuery))) {
            throw new BadRequestException("検索条件が不正です。");
        }
        Pageable pageable = PageableUtils.createPageable(
            page,
            size,
            sortString,
            PageableUtils.BOOK_ALLOWED_FIELDS
        );

        List<Long> genreIds = Arrays.stream(genreIdsQuery.split(","))
            .map(String::trim)
            .map(this::parseGenreId)
            .collect(Collectors.toList());

        Boolean isAndCondition = "AND".equals(conditionQuery);

        Page<Book> pageObj = isAndCondition
            ? bookRepository.findBooksHavingAllGenres(genreIds, genreIds.size(), pageable)
            : bookRepository.findDistinctByGenres_IdInAndIsDeletedFalse(genreIds, pageable);

        // 2クエリ戦略：IDリストから関連データを含むリストを取得
        List<String> ids = pageObj.getContent().stream().map(Book::getId).toList();
        List<Book> list = bookRepository.findAllByIdInWithRelations(ids);

        // ソート順序を復元
        List<Book> sortedList = PageableUtils.restoreSortOrder(ids, list, Book::getId);

        // 元のページネーション情報を保持して新しいPageオブジェクトを作成
        Page<Book> updatedPageObj = new PageImpl<>(
            sortedList,
            pageable,
            pageObj.getTotalElements()
        );

        return bookMapper.toPageResponse(updatedPageObj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BookDetailsResponse getBookDetails(String id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Book not found"));

        List<Long> bookGenreIds = book.getGenres().stream().map(Genre::getId).collect(Collectors.toList());

        List<GenreResponse> relevantGenres = genreService.getGenresByIds(bookGenreIds);

        BookDetailsResponse response = bookMapper.toBookDetailsResponse(book);
        response.setGenres(relevantGenres);

        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BookTableOfContentsResponse getBookTableOfContents(String id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Book not found"));

        List<BookChapter> chapters = bookChapterRepository.findByBookId(id);

        List<BookChapterResponse> chapterResponses = chapters.stream().map(chapter -> {
            Integer chapterNumber = chapter.getId().getChapterNumber();

            List<BookChapterPageContent> pageContents = bookChapterPageContentRepository
                .findByIdBookIdAndIdChapterNumber(id, chapterNumber);

            List<Integer> pageNumbers = pageContents.stream()
                .map(content -> content.getId().getPageNumber())
                .collect(Collectors.toList());

            BookChapterResponse response = new BookChapterResponse();
            response.setChapterNumber(chapterNumber);
            response.setChapterTitle(chapter.getTitle());
            response.setPageNumbers(pageNumbers);

            return response;
        }).collect(Collectors.toList());

        BookTableOfContentsResponse response = new BookTableOfContentsResponse();
        response.setBookId(id);
        response.setTitle(book.getTitle());
        response.setChapters(chapterResponses);

        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BookChapterPageContentResponse getBookChapterPageContent(
        String bookId,
        Integer chapterNumber,
        Integer pageNumber
    ) {
        BookChapterPageContentId pageContentId = new BookChapterPageContentId(
            bookId,
            chapterNumber,
            pageNumber
        );
        BookChapterId chapterId = new BookChapterId(bookId, chapterNumber);

        BookChapterPageContent pageContent = bookChapterPageContentRepository.findById(pageContentId)
            .orElseThrow(
                () -> new NotFoundException("BookChapterPageContent not found")
            );
        BookChapter chapter = bookChapterRepository.findById(chapterId)
            .orElseThrow(() -> new NotFoundException("BookChapter not found"));

        BookChapterPageContentResponse response = new BookChapterPageContentResponse();
        response.setBookId(bookId);
        response.setChapterNumber(chapterNumber);
        response.setChapterTitle(chapter.getTitle());
        response.setPageNumber(pageNumber);
        response.setContent(pageContent.getContent());

        return response;
    }

    // ----プライベートメソッド----

    private Long parseGenreId(String id) {
        try {
            return Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Invalid genre ID: " + id);
        }
    }
}
