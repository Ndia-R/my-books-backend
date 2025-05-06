package com.example.my_books_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.my_books_backend.dto.book.BookDetailsResponse;
import com.example.my_books_backend.dto.book.BookPageResponse;
import com.example.my_books_backend.dto.book_chapter.BookTableOfContentsResponse;
import com.example.my_books_backend.dto.book_chapter_page_content.BookChapterPageContentResponse;
import com.example.my_books_backend.dto.favorite.FavoriteCountsResponse;
import com.example.my_books_backend.dto.review.ReviewPageResponse;
import com.example.my_books_backend.dto.review.ReviewCountsResponse;
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

    // 最新の書籍リスト（１０冊分）
    @GetMapping("/new-releases")
    public ResponseEntity<BookPageResponse> getLatestBooks(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer maxResults) {
        BookPageResponse response = bookService.getLatestBooks(page, maxResults);
        return ResponseEntity.ok(response);
    }

    // タイトル検索
    @GetMapping("/search")
    public ResponseEntity<BookPageResponse> searchBooksByTitleKeyword(@RequestParam String q,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer maxResults) {
        BookPageResponse response = bookService.searchBooksByTitleKeyword(q, page, maxResults);
        return ResponseEntity.ok(response);
    }

    // ジャンル検索
    @GetMapping("/discover")
    public ResponseEntity<BookPageResponse> searchBooksByGenre(@RequestParam String genreIds,
            @RequestParam String condition, @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer maxResults) {
        BookPageResponse response =
                bookService.searchBooksByGenre(genreIds, condition, page, maxResults);
        return ResponseEntity.ok(response);
    }

    // 特定の書籍の詳細
    @GetMapping("/{bookId}")
    public ResponseEntity<BookDetailsResponse> getBookDetails(@PathVariable String bookId) {
        BookDetailsResponse response = bookService.getBookDetails(bookId);
        return ResponseEntity.ok(response);
    }

    // 特定の書籍の目次
    @GetMapping("/{bookId}/toc")
    public ResponseEntity<BookTableOfContentsResponse> getBookTableOfContents(
            @PathVariable String bookId) {
        BookTableOfContentsResponse response = bookService.getBookTableOfContents(bookId);
        return ResponseEntity.ok(response);
    }

    // 特定の書籍の閲覧ページ
    @GetMapping("/{bookId}/chapters/{chapterNumber}/pages/{pageNumber}")
    public ResponseEntity<BookChapterPageContentResponse> getBookChapterPageContent(
            @PathVariable String bookId, @PathVariable Integer chapterNumber,
            @PathVariable Integer pageNumber) {
        BookChapterPageContentResponse response =
                bookService.getBookChapterPageContent(bookId, chapterNumber, pageNumber);
        return ResponseEntity.ok(response);
    }

    // 特定の書籍のレビュー一覧
    @GetMapping("/{bookId}/reviews")
    public ResponseEntity<ReviewPageResponse> getBookReviews(@PathVariable String bookId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer maxResults) {
        ReviewPageResponse response = reviewService.getBookReviews(bookId, page, maxResults);
        return ResponseEntity.ok(response);
    }

    // 特定の書籍のレビュー数
    @GetMapping("/{bookId}/reviews/counts")
    public ResponseEntity<ReviewCountsResponse> getBookReviewCounts(@PathVariable String bookId) {
        ReviewCountsResponse response = reviewService.getBookReviewCounts(bookId);
        return ResponseEntity.ok(response);
    }

    // 特定の書籍のお気に入り数
    @GetMapping("/{bookId}/favorites/counts")
    public ResponseEntity<FavoriteCountsResponse> getBookFavoriteCounts(
            @PathVariable String bookId) {
        FavoriteCountsResponse response = favoriteService.getBookFavoriteCounts(bookId);
        return ResponseEntity.ok(response);
    }
}
