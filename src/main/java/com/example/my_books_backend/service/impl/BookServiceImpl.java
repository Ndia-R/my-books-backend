package com.example.my_books_backend.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.my_books_backend.dto.genre.GenreResponse;
import com.example.my_books_backend.dto.CursorPageResponse;
import com.example.my_books_backend.dto.book.BookDetailsResponse;
import com.example.my_books_backend.dto.book.BookPageResponse;
import com.example.my_books_backend.dto.book.BookResponse;
import com.example.my_books_backend.dto.book_chapter.BookChapterResponse;
import com.example.my_books_backend.dto.book_chapter.BookTableOfContentsResponse;
import com.example.my_books_backend.dto.book_chapter_page_content.BookChapterPageContentResponse;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.BookChapter;
import com.example.my_books_backend.entity.BookChapterId;
import com.example.my_books_backend.entity.BookChapterPageContent;
import com.example.my_books_backend.entity.BookChapterPageContentId;
import com.example.my_books_backend.entity.Review;
import com.example.my_books_backend.exception.BadRequestException;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.mapper.BookMapper;
import com.example.my_books_backend.repository.BookChapterRepository;
import com.example.my_books_backend.repository.BookChapterPageContentRepository;
import com.example.my_books_backend.repository.BookRepository;
import com.example.my_books_backend.repository.ReviewRepository;
import com.example.my_books_backend.service.BookService;
import com.example.my_books_backend.service.GenreService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;
    private final BookChapterRepository bookChapterRepository;
    private final BookChapterPageContentRepository bookChapterPageContentRepository;
    private final BookMapper bookMapper;

    private final GenreService genreService;

    /**
     * {@inheritDoc}
     */
    @Override
    public BookPageResponse getLatestBooks(Pageable pageable) {
        Page<Book> books = bookRepository.findByIsDeletedFalse(pageable);

        Map<String, Integer> reviewCounts = new HashMap<>();
        Map<String, Double> averageRatings = new HashMap<>();
        loadReviewStatistics(books.getContent(), reviewCounts, averageRatings);

        return bookMapper.toBookPageResponse(books, reviewCounts, averageRatings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BookPageResponse getBooksByTitleKeyword(String keyword, Pageable pageable) {
        Page<Book> books = bookRepository.findByTitleContainingAndIsDeletedFalse(keyword, pageable);

        Map<String, Integer> reviewCounts = new HashMap<>();
        Map<String, Double> averageRatings = new HashMap<>();
        loadReviewStatistics(books.getContent(), reviewCounts, averageRatings);

        return bookMapper.toBookPageResponse(books, reviewCounts, averageRatings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CursorPageResponse<BookResponse> getBooksByTitleKeywordWithCursor(String keyword,
            String cursor, Integer limit) {
        // 次のページの有無を判定するために、1件多く取得
        List<Book> books = bookRepository.findBooksByTitleKeywordWithCursor("%" + keyword + "%",
                cursor, limit + 1);

        Boolean hasNext = books.size() > limit;
        if (hasNext) {
            books = books.subList(0, limit); // 余分な1件を削除
        }

        String endCursor = hasNext ? books.get(books.size() - 1).getId() : null;

        Map<String, Integer> reviewCounts = new HashMap<>();
        Map<String, Double> averageRatings = new HashMap<>();
        loadReviewStatistics(books, reviewCounts, averageRatings);

        return bookMapper.toCursorPageResponse(books, endCursor, hasNext, reviewCounts,
                averageRatings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BookPageResponse getBooksByGenre(String genreIdsQuery, String conditionQuery,
            Pageable pageable) {
        if (!("SINGLE".equals(conditionQuery) || "AND".equals(conditionQuery)
                || "OR".equals(conditionQuery))) {
            throw new BadRequestException("検索条件が不正です。");
        }
        List<Long> genreIds =
                Arrays.stream(genreIdsQuery.split(",")).map(id -> Long.parseLong(id)).toList();

        Boolean isAndCondition = "AND".equals(conditionQuery);

        Page<Book> books = isAndCondition
                ? bookRepository.findDistinctByGenres_IdInAndIsDeletedFalse(genreIds,
                        genreIds.size(), pageable)
                : bookRepository.findDistinctByGenres_IdIn(genreIds, pageable);

        Map<String, Integer> reviewCounts = new HashMap<>();
        Map<String, Double> averageRatings = new HashMap<>();
        loadReviewStatistics(books.getContent(), reviewCounts, averageRatings);

        return bookMapper.toBookPageResponse(books, reviewCounts, averageRatings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BookDetailsResponse getBookDetails(String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found"));

        List<GenreResponse> allGenres = genreService.getAllGenres();

        List<Long> bookGenreIds = book.getGenres().stream().map(genre -> genre.getId()).toList();

        List<GenreResponse> relevantGenres =
                allGenres.stream().filter(genre -> bookGenreIds.contains(genre.getId())).toList();

        // レビュー情報を取得
        List<Review> reviews = reviewRepository.findByBookIdAndIsDeletedFalse(book.getId());
        Integer reviewCount = reviews.size();
        Double averageRating =
                reviews.stream().mapToDouble(review -> review.getRating()).average().orElse(0.0);

        BookDetailsResponse response =
                bookMapper.toBookDetailsResponse(book, reviewCount, averageRating);
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

            List<Integer> pageNumbers =
                    pageContents.stream().map(content -> content.getId().getPageNumber()).toList();

            BookChapterResponse response = new BookChapterResponse();
            response.setChapterNumber(chapterNumber);
            response.setChapterTitle(chapter.getTitle());
            response.setPageNumbers(pageNumbers);

            return response;
        }).toList();

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadReviewStatistics(List<Book> books, Map<String, Integer> reviewCounts,
            Map<String, Double> averageRatings) {
        if (books.isEmpty()) {
            return;
        }

        List<String> bookIds = books.stream().map(book -> book.getId()).toList();

        // 一度のクエリでレビュー数と平均評価を取得
        List<Object[]> reviewStats = reviewRepository.findReviewStatsByBookIds(bookIds);

        for (Object[] stat : reviewStats) {
            String bookId = (String) stat[0];
            Integer count = ((Number) stat[1]).intValue();
            Double avgRating = stat[2] != null ? ((Number) stat[2]).doubleValue() : 0.0;

            reviewCounts.put(bookId, count);
            averageRatings.put(bookId, avgRating);
        }

        // レビューが存在しない書籍にデフォルト値を設定
        for (Book book : books) {
            reviewCounts.putIfAbsent(book.getId(), 0);
            averageRatings.putIfAbsent(book.getId(), 0.0);
        }
    }
}
