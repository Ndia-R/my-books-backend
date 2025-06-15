package com.example.my_books_backend.controller;

import org.springframework.data.domain.Pageable;
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
import com.example.my_books_backend.util.PageableUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final ReviewService reviewService;
    private final FavoriteService favoriteService;

    private static final String DEFAULT_BOOKS_START_PAGE = "1";
    private static final String DEFAULT_BOOKS_PAGE_SIZE = "20";
    private static final String DEFAULT_BOOKS_SORT = "publicationDate.desc";

    private static final String DEFAULT_REVIEWS_START_PAGE = "1";
    private static final String DEFAULT_REVIEWS_PAGE_SIZE = "3";
    private static final String DEFAULT_REVIEWS_SORT = "updatedAt.desc";

    @Operation(description = "最新の書籍リスト（１０冊）")
    @GetMapping("/new-releases")
    public ResponseEntity<PageResponse<BookResponse>> getLatestBooks() {
        PageResponse<BookResponse> response = bookService.getBooks(1, 10, "publicationDate.desc");
        return ResponseEntity.ok(response);
    }

    @Operation(description = "タイトル検索: 指定されたタイトルから書籍を検索")
    @GetMapping("/search")
    public ResponseEntity<PageResponse<BookResponse>> getBooksByTitleKeyword(

            @Parameter(description = "タイトルに指定された文字列を含む書籍を検索", example = "魔法",
                    required = true) @RequestParam String q,

            @Parameter(description = "ページ番号（1ベース）",
                    example = DEFAULT_BOOKS_START_PAGE) @RequestParam(
                            defaultValue = DEFAULT_BOOKS_START_PAGE) Integer page,

            @Parameter(description = "1ページあたりの件数", example = DEFAULT_BOOKS_PAGE_SIZE) @RequestParam(
                    defaultValue = DEFAULT_BOOKS_PAGE_SIZE) Integer size,

            @Parameter(description = "ソート条件", example = DEFAULT_BOOKS_SORT,
                    schema = @Schema(allowableValues = {"title.asc", "title.desc",
                            "publicationDate.asc", "publicationDate.desc", "averageRating.asc",
                            "averageRating.desc", "reviewCount.asc",
                            "reviewCount.desc"})) @RequestParam(
                                    defaultValue = DEFAULT_BOOKS_SORT) String sort) {

        PageResponse<BookResponse> response =
                bookService.getBooksByTitleKeyword(q, page, size, sort);
        return ResponseEntity.ok(response);
    }

    @Operation(description = "タイトル検索（カーソルベース）: 指定されたタイトルから書籍を検索")
    @GetMapping("/search/cursor")
    public ResponseEntity<CursorPageResponse<BookResponse>> getBooksByTitleKeywordWithCursor(

            @Parameter(description = "タイトルに指定された文字列を含む書籍を検索", example = "魔法",
                    required = true) @RequestParam String q,

            @Parameter(description = "カーソル（次のページを取得するための起点となるID）") @RequestParam(
                    required = false) String cursor,

            @Parameter(description = "1ページあたりの件数", example = DEFAULT_BOOKS_PAGE_SIZE) @RequestParam(
                    defaultValue = DEFAULT_BOOKS_PAGE_SIZE) Integer limit,

            @Parameter(description = "ソート条件", example = DEFAULT_BOOKS_SORT,
                    schema = @Schema(allowableValues = {"title.asc", "title.desc",
                            "publicationDate.asc", "publicationDate.desc", "averageRating.asc",
                            "averageRating.desc", "reviewCount.asc",
                            "reviewCount.desc"})) @RequestParam(
                                    defaultValue = DEFAULT_BOOKS_SORT) String sort) {

        CursorPageResponse<BookResponse> response =
                bookService.getBooksByTitleKeywordWithCursor(q, cursor, limit, sort);
        return ResponseEntity.ok(response);
    }

    @Operation(description = "ジャンル検索: 指定されたジャンルIDと条件に基づいて書籍を検索")
    @GetMapping("/discover")
    public ResponseEntity<PageResponse<BookResponse>> getBooksByGenre(

            @Parameter(description = """
                    検索対象のジャンルIDをカンマ区切りで指定
                    - 単一ジャンル: 1
                    - 複数ジャンル: 1,2,3
                    """, example = "1,2", required = true) @RequestParam String genreIds,

            @Parameter(description = """
                    ジャンル検索の条件を指定
                    - SINGLE: 指定したジャンルのみ（複数指定の場合、最初のジャンルのみ）
                    - AND: 指定したすべてのジャンル
                    - OR: 指定したいずれかのジャンル
                    """, example = "AND", required = true, schema = @Schema(
                    allowableValues = {"SINGLE", "AND", "OR"})) @RequestParam String condition,

            @Parameter(description = "ページ番号（1ベース）",
                    example = DEFAULT_BOOKS_START_PAGE) @RequestParam(
                            defaultValue = DEFAULT_BOOKS_START_PAGE) Integer page,

            @Parameter(description = "1ページあたりの件数", example = DEFAULT_BOOKS_PAGE_SIZE) @RequestParam(
                    defaultValue = DEFAULT_BOOKS_PAGE_SIZE) Integer size,

            @Parameter(description = "ソート条件", example = DEFAULT_BOOKS_SORT,
                    schema = @Schema(allowableValues = {"title.asc", "title.desc",
                            "publicationDate.asc", "publicationDate.desc", "averageRating.asc",
                            "averageRating.desc", "reviewCount.asc",
                            "reviewCount.desc"})) @RequestParam(
                                    defaultValue = DEFAULT_BOOKS_SORT) String sort) {

        Pageable pageable = PageableUtils.createBookPageable(page, size, sort);
        PageResponse<BookResponse> response =
                bookService.getBooksByGenre(genreIds, condition, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(description = "特定の書籍の詳細")
    @GetMapping("/{id}")
    public ResponseEntity<BookDetailsResponse> getBookDetails(@PathVariable String id) {
        BookDetailsResponse response = bookService.getBookDetails(id);
        return ResponseEntity.ok(response);
    }

    @Operation(description = "特定の書籍の目次")
    @GetMapping("/{id}/toc")
    public ResponseEntity<BookTableOfContentsResponse> getBookTableOfContents(
            @PathVariable String id) {
        BookTableOfContentsResponse response = bookService.getBookTableOfContents(id);
        return ResponseEntity.ok(response);
    }

    @Operation(description = "特定の書籍の閲覧ページ")
    @GetMapping("/{id}/chapters/{chapter}/pages/{page}")
    public ResponseEntity<BookChapterPageContentResponse> getBookChapterPageContent(
            @PathVariable String id, @PathVariable Integer chapter, @PathVariable Integer page) {
        BookChapterPageContentResponse response =
                bookService.getBookChapterPageContent(id, chapter, page);
        return ResponseEntity.ok(response);
    }

    @Operation(description = "特定の書籍のレビューリスト")
    @GetMapping("/{id}/reviews")
    public ResponseEntity<PageResponse<ReviewResponse>> getBookReviews(

            @PathVariable String id,

            @Parameter(description = "ページ番号（1ベース）",
                    example = DEFAULT_REVIEWS_START_PAGE) @RequestParam(
                            defaultValue = DEFAULT_REVIEWS_START_PAGE) Integer page,

            @Parameter(description = "1ページあたりの件数",
                    example = DEFAULT_REVIEWS_PAGE_SIZE) @RequestParam(
                            defaultValue = DEFAULT_REVIEWS_PAGE_SIZE) Integer size,

            @Parameter(description = "ソート条件", example = DEFAULT_REVIEWS_SORT,
                    schema = @Schema(
                            allowableValues = {"updatedAt.asc", "updatedAt.desc", "createdAt.asc",
                                    "createdAt.desc", "rating.asc", "rating.desc"})) @RequestParam(
                                            defaultValue = DEFAULT_REVIEWS_SORT) String sort) {

        PageResponse<ReviewResponse> response = reviewService.getBookReviews(id, page, size, sort);
        return ResponseEntity.ok(response);
    }

    @Operation(description = "特定の書籍のレビューリスト（カーソルベース）")
    @GetMapping("/{id}/reviews/cursor")
    public ResponseEntity<CursorPageResponse<ReviewResponse>> getBookReviewsWithCursor(

            @PathVariable String id,

            @Parameter(description = "カーソル（次のページを取得するための起点となるID）") @RequestParam(
                    required = false) Long cursor,

            @Parameter(description = "1ページあたりの件数",
                    example = DEFAULT_REVIEWS_PAGE_SIZE) @RequestParam(
                            defaultValue = DEFAULT_REVIEWS_PAGE_SIZE) Integer limit,

            @Parameter(description = "ソート条件", example = DEFAULT_REVIEWS_SORT,
                    schema = @Schema(
                            allowableValues = {"updatedAt.asc", "updatedAt.desc", "createdAt.asc",
                                    "createdAt.desc", "rating.asc", "rating.desc"})) @RequestParam(
                                            defaultValue = DEFAULT_REVIEWS_SORT) String sort) {

        CursorPageResponse<ReviewResponse> response =
                reviewService.getBookReviewsWithCursor(id, cursor, limit, sort);
        return ResponseEntity.ok(response);
    }

    @Operation(description = "特定の書籍のレビュー数")
    @GetMapping("/{id}/reviews/counts")
    public ResponseEntity<ReviewCountsResponse> getBookReviewCounts(@PathVariable String id) {
        ReviewCountsResponse response = reviewService.getBookReviewCounts(id);
        return ResponseEntity.ok(response);
    }

    @Operation(description = "特定の書籍のお気に入り数")
    @GetMapping("/{id}/favorites/counts")
    public ResponseEntity<FavoriteCountsResponse> getBookFavoriteCounts(@PathVariable String id) {
        FavoriteCountsResponse response = favoriteService.getBookFavoriteCounts(id);
        return ResponseEntity.ok(response);
    }
}
