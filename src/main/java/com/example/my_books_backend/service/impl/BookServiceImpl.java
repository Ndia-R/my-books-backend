package com.example.my_books_backend.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.example.my_books_backend.dto.genre.GenreResponse;
import com.example.my_books_backend.dto.book.BookDetailsResponse;
import com.example.my_books_backend.dto.book.BookPageResponse;
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
import com.example.my_books_backend.repository.BookChapterPageContentRepository;
import com.example.my_books_backend.repository.BookRepository;
import com.example.my_books_backend.service.BookService;
import com.example.my_books_backend.service.GenreService;
import com.example.my_books_backend.util.PaginationUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookChapterRepository bookChapterRepository;
    private final BookChapterPageContentRepository bookChapterPageContentRepository;
    private final BookMapper bookMapper;

    private final PaginationUtil paginationUtil;
    private final GenreService genreService;

    /** 書籍一覧のデフォルトソート（出版日） */
    private static final Sort DEFAULT_SORT =
            Sort.by(Sort.Order.desc("publicationDate"), Sort.Order.asc("id"));

    /**
     * {@inheritDoc}
     */
    @Override
    public BookPageResponse getLatestBooks(Integer page, Integer maxResults) {
        Pageable pageable = paginationUtil.createPageable(page, maxResults, DEFAULT_SORT);
        Page<Book> books = bookRepository.findTop10ByOrderByPublicationDateDesc(pageable);
        return bookMapper.toBookPageResponse(books);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BookPageResponse searchBooksByTitleKeyword(String keyword, Integer page,
            Integer maxResults) {
        Pageable pageable = paginationUtil.createPageable(page, maxResults, DEFAULT_SORT);
        Page<Book> books = bookRepository.findByTitleContaining(keyword, pageable);
        return bookMapper.toBookPageResponse(books);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BookPageResponse searchBooksByGenre(String genreIdsQuery, String conditionQuery,
            Integer page, Integer maxResults) {
        if (!("SINGLE".equals(conditionQuery) || "AND".equals(conditionQuery)
                || "OR".equals(conditionQuery))) {
            throw new BadRequestException("検索条件が不正です。");
        }

        Pageable pageable = paginationUtil.createPageable(page, maxResults, DEFAULT_SORT);

        List<Long> genreIds = Arrays.stream(genreIdsQuery.split(",")).map(Long::parseLong)
                .collect(Collectors.toList());

        Boolean isAndCondition = "AND".equals(conditionQuery);

        Page<Book> books = isAndCondition
                ? bookRepository.findByAllGenreIds(genreIds, genreIds.size(), pageable)
                : bookRepository.findDistinctByGenres_IdIn(genreIds, pageable);

        return bookMapper.toBookPageResponse(books);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BookDetailsResponse getBookDetails(String bookId) {
        Book book = bookRepository.findById(bookId)
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
    public BookTableOfContentsResponse getBookTableOfContents(String bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found"));

        List<BookChapter> chapters = bookChapterRepository.findByBookId(bookId);

        List<BookChapterResponse> chapterResponses = chapters.stream().map(chapter -> {
            Integer chapterNumber = chapter.getId().getChapterNumber();

            List<BookChapterPageContent> pageContents = bookChapterPageContentRepository
                    .findByIdBookIdAndIdChapterNumber(bookId, chapterNumber);

            List<Integer> pageNumbers = pageContents.stream()
                    .map(content -> content.getId().getPageNumber()).collect(Collectors.toList());

            BookChapterResponse response = new BookChapterResponse();
            response.setChapterNumber(chapterNumber);
            response.setChapterTitle(chapter.getTitle());
            response.setPageNumbers(pageNumbers);

            return response;
        }).collect(Collectors.toList());

        BookTableOfContentsResponse response = new BookTableOfContentsResponse();
        response.setBookId(bookId);
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
