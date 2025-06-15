package com.example.my_books_backend.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.example.my_books_backend.dto.genre.GenreResponse;
import com.example.my_books_backend.dto.CursorPageResponse;
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
import com.example.my_books_backend.repository.book.BookRepository;
import com.example.my_books_backend.repository.BookChapterPageContentRepository;
import com.example.my_books_backend.service.BookService;
import com.example.my_books_backend.service.GenreService;
import com.example.my_books_backend.util.PageableUtils;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
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
    public PageResponse<BookResponse> getBooks(Integer page, Integer size, String sortString) {
        Pageable pageable = PageableUtils.createBookPageable(page, size, sortString);
        Page<Book> books = bookRepository.findByIsDeletedFalse(pageable);
        return bookMapper.toPageResponse(books);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageResponse<BookResponse> getBooksByTitleKeyword(String keyword, Integer page,
            Integer size, String sortString) {
        Pageable pageable = PageableUtils.createBookPageable(page, size, sortString);
        Page<Book> books = bookRepository.findByTitleContainingAndIsDeletedFalse(keyword, pageable);
        return bookMapper.toPageResponse(books);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CursorPageResponse<BookResponse> getBooksByTitleKeywordWithCursor(String keyword,
            String cursor, Integer limit, String sortString) {

        Sort sort = PageableUtils.parseSort(sortString, PageableUtils.BOOK_ALLOWED_FIELDS);
        String sortField = sort.iterator().next().getProperty();
        String sortDirection = sort.iterator().next().getDirection().name().toLowerCase();

        // 次のページの有無を判定するために、limit + 1にして、1件多く取得
        List<Book> books = bookRepository.findBooksByTitleKeywordWithCursor(keyword, cursor,
                limit + 1, sortField, sortDirection);
        return bookMapper.toCursorPageResponse(books, limit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageResponse<BookResponse> getBooksByGenre(String genreIdsQuery, String conditionQuery,
            Pageable pageable) {
        if (!("SINGLE".equals(conditionQuery) || "AND".equals(conditionQuery)
                || "OR".equals(conditionQuery))) {
            throw new BadRequestException("検索条件が不正です。");
        }
        List<Long> genreIds = Arrays.stream(genreIdsQuery.split(",")).map(Long::parseLong)
                .collect(Collectors.toList());

        Boolean isAndCondition = "AND".equals(conditionQuery);

        Page<Book> books = isAndCondition
                ? bookRepository.findBooksHavingAllGenres(genreIds, genreIds.size(), pageable)
                : bookRepository.findDistinctByGenres_IdInAndIsDeletedFalse(genreIds, pageable);

        return bookMapper.toPageResponse(books);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BookDetailsResponse getBookDetails(String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found"));

        List<GenreResponse> allGenres = genreService.getAllGenres();

        List<Long> bookGenreIds =
                book.getGenres().stream().map(Genre::getId).collect(Collectors.toList());

        List<GenreResponse> relevantGenres = allGenres.stream()
                .filter(genre -> bookGenreIds.contains(genre.getId())).collect(Collectors.toList());

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
                    .map(content -> content.getId().getPageNumber()).collect(Collectors.toList());

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
    public BookChapterPageContentResponse getBookChapterPageContent(String bookId,
            Integer chapterNumber, Integer pageNumber) {
        BookChapterPageContentId pageContentId =
                new BookChapterPageContentId(bookId, chapterNumber, pageNumber);
        BookChapterId chapterId = new BookChapterId(bookId, chapterNumber);

        BookChapterPageContent pageContent =
                bookChapterPageContentRepository.findById(pageContentId).orElseThrow(
                        () -> new NotFoundException("BookChapterPageContent not found"));
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
}
