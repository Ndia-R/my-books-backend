package com.example.my_books_backend.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.my_books_backend.dto.CursorPageResponse;
import com.example.my_books_backend.dto.PageResponse;
import com.example.my_books_backend.dto.book.BookDetailsResponse;
import com.example.my_books_backend.dto.book.BookResponse;
import com.example.my_books_backend.dto.book_chapter.BookTableOfContentsResponse;
import com.example.my_books_backend.dto.book_chapter_page_content.BookChapterPageContentResponse;
import com.example.my_books_backend.dto.favorite.FavoriteCountsResponse;
import com.example.my_books_backend.dto.review.ReviewCountsResponse;
import com.example.my_books_backend.dto.review.ReviewResponse;
import com.example.my_books_backend.service.BookService;
import com.example.my_books_backend.service.FavoriteService;
import com.example.my_books_backend.service.ReviewService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final ReviewService reviewService;
    private final FavoriteService favoriteService;

    private static final int LATEST_BOOKS_PAGE_SIZE = 10;

    private static final int DEFAULT_BOOKS_PAGE_SIZE = 20;
    private static final String DEFAULT_BOOKS_PAGE_SIZE_STR = "20"; // カーソルベース用

    private static final int DEFAULT_REVIEWS_PAGE_SIZE = 3;
    private static final String DEFAULT_REVIEWS_PAGE_SIZE_STR = "3"; // カーソルベース用

    // Pageableの引数についている「@ParameterObject」はSwaggerでpageableを個別クエリパラメータとして指定したいため

    // 最新の書籍リスト
    @GetMapping("/new-releases")
    public ResponseEntity<PageResponse<BookResponse>> getLatestBooks(
            @ParameterObject @PageableDefault(size = LATEST_BOOKS_PAGE_SIZE,
                    sort = {"publicationDate", "id"},
                    direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<BookResponse> response = bookService.getLatestBooks(pageable);
        return ResponseEntity.ok(response);
    }

    // タイトル検索
    @GetMapping("/search")
    public ResponseEntity<PageResponse<BookResponse>> getBooksByTitleKeyword(@RequestParam String q,
            @ParameterObject @PageableDefault(size = DEFAULT_BOOKS_PAGE_SIZE,
                    sort = {"publicationDate", "id"},
                    direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<BookResponse> response = bookService.getBooksByTitleKeyword(q, pageable);
        return ResponseEntity.ok(response);
    }

    // タイトル検索（カーソルベース）
    @GetMapping("/search/cursor")
    public ResponseEntity<CursorPageResponse<BookResponse>> getBooksByTitleKeywordWithCursor(
            @RequestParam String q, @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = DEFAULT_BOOKS_PAGE_SIZE_STR) Integer limit) {
        CursorPageResponse<BookResponse> response =
                bookService.getBooksByTitleKeywordWithCursor(q, cursor, limit);
        return ResponseEntity.ok(response);
    }

    // ジャンル検索
    @GetMapping("/discover")
    public ResponseEntity<PageResponse<BookResponse>> getBooksByGenre(@RequestParam String genreIds,
            @RequestParam String condition,
            @ParameterObject @PageableDefault(size = DEFAULT_BOOKS_PAGE_SIZE,
                    sort = {"publicationDate", "id"},
                    direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<BookResponse> response =
                bookService.getBooksByGenre(genreIds, condition, pageable);
        return ResponseEntity.ok(response);
    }

    // 特定の書籍の詳細
    @GetMapping("/{id}")
    public ResponseEntity<BookDetailsResponse> getBookDetails(@PathVariable String id) {
        BookDetailsResponse response = bookService.getBookDetails(id);
        return ResponseEntity.ok(response);
    }

    // 特定の書籍の目次
    @GetMapping("/{id}/toc")
    public ResponseEntity<BookTableOfContentsResponse> getBookTableOfContents(
            @PathVariable String id) {
        BookTableOfContentsResponse response = bookService.getBookTableOfContents(id);
        return ResponseEntity.ok(response);
    }

    // 特定の書籍の閲覧ページ
    @GetMapping("/{id}/chapters/{chapter}/pages/{page}")
    public ResponseEntity<BookChapterPageContentResponse> getBookChapterPageContent(
            @PathVariable String id, @PathVariable Integer chapter, @PathVariable Integer page) {
        BookChapterPageContentResponse response =
                bookService.getBookChapterPageContent(id, chapter, page);
        return ResponseEntity.ok(response);
    }

    // 特定の書籍のレビュー一覧
    @GetMapping("/{id}/reviews")
    public ResponseEntity<PageResponse<ReviewResponse>> getBookReviews(@PathVariable String id,
            @ParameterObject @PageableDefault(size = DEFAULT_REVIEWS_PAGE_SIZE,
                    sort = {"updatedAt", "id"},
                    direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<ReviewResponse> response = reviewService.getBookReviews(id, pageable);
        return ResponseEntity.ok(response);
    }

    // 特定の書籍のレビュー一覧（カーソルベース）
    @GetMapping("/{id}/reviews/cursor")
    public ResponseEntity<CursorPageResponse<ReviewResponse>> getBookReviewsWithCursor(
            @PathVariable String id, @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = DEFAULT_REVIEWS_PAGE_SIZE_STR) Integer limit) {
        CursorPageResponse<ReviewResponse> response =
                reviewService.getBookReviewsWithCursor(id, cursor, limit);
        return ResponseEntity.ok(response);
    }

    // 特定の書籍のレビュー数
    @GetMapping("/{id}/reviews/counts")
    public ResponseEntity<ReviewCountsResponse> getBookReviewCounts(@PathVariable String id) {
        ReviewCountsResponse response = reviewService.getBookReviewCounts(id);
        return ResponseEntity.ok(response);
    }

    // 特定の書籍のお気に入り数
    @GetMapping("/{id}/favorites/counts")
    public ResponseEntity<FavoriteCountsResponse> getBookFavoriteCounts(@PathVariable String id) {
        FavoriteCountsResponse response = favoriteService.getBookFavoriteCounts(id);
        return ResponseEntity.ok(response);
    }
}
