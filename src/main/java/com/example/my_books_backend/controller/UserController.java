package com.example.my_books_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.my_books_backend.dto.user.UserProfileCountsResponse;
import com.example.my_books_backend.dto.user.UserProfileResponse;
import com.example.my_books_backend.dto.PageResponse;
import com.example.my_books_backend.dto.SliceResponse;
import com.example.my_books_backend.dto.bookmark.BookmarkResponse;
import com.example.my_books_backend.dto.favorite.FavoriteResponse;
import com.example.my_books_backend.dto.review.ReviewResponse;
import com.example.my_books_backend.dto.user.UpdateUserEmailRequest;
import com.example.my_books_backend.dto.user.UpdateUserPasswordRequest;
import com.example.my_books_backend.dto.user.UpdateUserProfileRequest;
import com.example.my_books_backend.entity.User;
import com.example.my_books_backend.service.BookmarkService;
import com.example.my_books_backend.service.FavoriteService;
import com.example.my_books_backend.service.ReviewService;
import com.example.my_books_backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final ReviewService reviewService;
    private final FavoriteService favoriteService;
    private final BookmarkService bookmarkService;

    private static final String DEFAULT_START_PAGE = "1";
    private static final String DEFAULT_PAGE_SIZE = "5";
    private static final String DEFAULT_SORT = "updatedAt.desc";
    private static final String DEFAULT_RESPONSE_TYPE = "page";

    @Operation(description = "ユーザーのプロフィール情報")
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(@AuthenticationPrincipal User user) {
        UserProfileResponse response = userService.getUserProfile(user);
        return ResponseEntity.ok(response);
    }

    @Operation(description = "ユーザーのレビュー、お気に入り、ブックマークの数")
    @GetMapping("/profile-counts")
    public ResponseEntity<UserProfileCountsResponse> getUserProfileCounts(
        @AuthenticationPrincipal User user
    ) {
        UserProfileCountsResponse response = userService.getUserProfileCounts(user);
        return ResponseEntity.ok(response);
    }

    @Operation(description = "ユーザーが投稿したレビューリスト")
    @GetMapping("/reviews")
    public ResponseEntity<?> getUserReviews(
        @AuthenticationPrincipal User user,
        @Parameter(description = "ページ番号（1ベース）", example = DEFAULT_START_PAGE) @RequestParam(defaultValue = DEFAULT_START_PAGE) Integer page,
        @Parameter(description = "1ページあたりの件数", example = DEFAULT_PAGE_SIZE) @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) Integer size,
        @Parameter(description = "ソート条件", example = DEFAULT_SORT, schema = @Schema(allowableValues = {
            "updatedAt.asc",
            "updatedAt.desc",
            "createdAt.asc",
            "createdAt.desc",
            "rating.asc",
            "rating.desc" })) @RequestParam(defaultValue = DEFAULT_SORT) String sort,
        @Parameter(description = "レスポンスタイプ（ページネーション用or無限スクロール用）", example = DEFAULT_RESPONSE_TYPE, schema = @Schema(allowableValues = {
            "page",
            "scroll" })) @RequestParam(defaultValue = DEFAULT_RESPONSE_TYPE) String responseType,
        @RequestParam(required = false) String bookId
    ) {
        if ("scroll".equals(responseType)) {
            SliceResponse<ReviewResponse> response = reviewService.getUserReviewsForScroll(
                user,
                page,
                size,
                sort,
                bookId
            );
            return ResponseEntity.ok(response);
        } else {
            PageResponse<ReviewResponse> response = reviewService.getUserReviews(
                user,
                page,
                size,
                sort,
                bookId
            );
            return ResponseEntity.ok(response);
        }
    }

    @Operation(description = "ユーザーが追加したお気に入りリスト")
    @GetMapping("/favorites")
    public ResponseEntity<?> getUserFavorites(
        @AuthenticationPrincipal User user,
        @Parameter(description = "ページ番号（1ベース）", example = DEFAULT_START_PAGE) @RequestParam(defaultValue = DEFAULT_START_PAGE) Integer page,
        @Parameter(description = "1ページあたりの件数", example = DEFAULT_PAGE_SIZE) @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) Integer size,
        @Parameter(description = "ソート条件", example = DEFAULT_SORT, schema = @Schema(allowableValues = {
            "updatedAt.asc",
            "updatedAt.desc",
            "createdAt.asc",
            "createdAt.desc" })) @RequestParam(defaultValue = DEFAULT_SORT) String sort,
        @Parameter(description = "レスポンスタイプ（ページネーション用or無限スクロール用）", example = DEFAULT_RESPONSE_TYPE, schema = @Schema(allowableValues = {
            "page",
            "scroll" })) @RequestParam(defaultValue = DEFAULT_RESPONSE_TYPE) String responseType,
        @RequestParam(required = false) String bookId
    ) {
        if ("scroll".equals(responseType)) {
            SliceResponse<FavoriteResponse> response = favoriteService.getUserFavoritesForScroll(
                user,
                page,
                size,
                sort,
                bookId
            );
            return ResponseEntity.ok(response);
        } else {
            PageResponse<FavoriteResponse> response = favoriteService.getUserFavorites(
                user,
                page,
                size,
                sort,
                bookId
            );
            return ResponseEntity.ok(response);
        }
    }

    @Operation(description = "ユーザーが追加したブックマークリスト")
    @GetMapping("/bookmarks")
    public ResponseEntity<?> getUserBookmarks(
        @AuthenticationPrincipal User user,
        @Parameter(description = "ページ番号（1ベース）", example = DEFAULT_START_PAGE) @RequestParam(defaultValue = DEFAULT_START_PAGE) Integer page,
        @Parameter(description = "1ページあたりの件数", example = DEFAULT_PAGE_SIZE) @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) Integer size,
        @Parameter(description = "ソート条件", example = DEFAULT_SORT, schema = @Schema(allowableValues = {
            "updatedAt.asc",
            "updatedAt.desc",
            "createdAt.asc",
            "createdAt.desc" })) @RequestParam(defaultValue = DEFAULT_SORT) String sort,
        @Parameter(description = "レスポンスタイプ（ページネーション用or無限スクロール用）", example = DEFAULT_RESPONSE_TYPE, schema = @Schema(allowableValues = {
            "page",
            "scroll" })) @RequestParam(defaultValue = DEFAULT_RESPONSE_TYPE) String responseType,
        @RequestParam(required = false) String bookId
    ) {
        if ("scroll".equals(responseType)) {
            SliceResponse<BookmarkResponse> responses = bookmarkService.getUserBookmarksForScroll(
                user,
                page,
                size,
                sort,
                bookId
            );
            return ResponseEntity.ok(responses);
        } else {
            PageResponse<BookmarkResponse> responses = bookmarkService.getUserBookmarks(
                user,
                page,
                size,
                sort,
                bookId
            );
            return ResponseEntity.ok(responses);
        }
    }

    @Operation(description = "ユーザーのプロフィール情報を更新")
    @PutMapping("/profile")
    public ResponseEntity<Void> updateUserProfile(
        @Valid @RequestBody UpdateUserProfileRequest request,
        @AuthenticationPrincipal User user
    ) {
        userService.updateUserProfile(request, user);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "ユーザーのメールアドレスを更新")
    @PutMapping("/email")
    public ResponseEntity<Void> updateUserEmail(
        @Valid @RequestBody UpdateUserEmailRequest request,
        @AuthenticationPrincipal User user
    ) {
        userService.updateUserEmail(request, user);
        return ResponseEntity.noContent().build();
    }

    @Operation(description = "ユーザーのパスワードを更新")
    @PutMapping("/password")
    public ResponseEntity<Void> updateUserPassword(
        @Valid @RequestBody UpdateUserPasswordRequest request,
        @AuthenticationPrincipal User user
    ) {
        userService.updateUserPassword(request, user);
        return ResponseEntity.noContent().build();
    }
}
