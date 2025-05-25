package com.example.my_books_backend.controller;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
import com.example.my_books_backend.dto.bookmark.BookmarkCursorResponse;
import com.example.my_books_backend.dto.bookmark.BookmarkPageResponse;
import com.example.my_books_backend.dto.favorite.FavoriteCursorResponse;
import com.example.my_books_backend.dto.favorite.FavoritePageResponse;
import com.example.my_books_backend.dto.review.ReviewCursorResponse;
import com.example.my_books_backend.dto.review.ReviewPageResponse;
import com.example.my_books_backend.dto.user.UpdateUserEmailRequest;
import com.example.my_books_backend.dto.user.UpdateUserPasswordRequest;
import com.example.my_books_backend.dto.user.UpdateUserProfileRequest;
import com.example.my_books_backend.entity.User;
import com.example.my_books_backend.service.BookmarkService;
import com.example.my_books_backend.service.FavoriteService;
import com.example.my_books_backend.service.ReviewService;
import com.example.my_books_backend.service.UserService;
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

    private static final int DEFAULT_PAGE_SIZE = 5;
    private static final String DEFAULT_PAGE_SIZE_STR = "5";

    // ユーザーのプロフィール情報
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(@AuthenticationPrincipal User user) {
        UserProfileResponse response = userService.getUserProfile(user);
        return ResponseEntity.ok(response);
    }

    // ユーザーのレビュー、お気に入り、ブックマークの数
    @GetMapping("/profile-counts")
    public ResponseEntity<UserProfileCountsResponse> getUserProfileCounts(
            @AuthenticationPrincipal User user) {
        UserProfileCountsResponse response = userService.getUserProfileCounts(user);
        return ResponseEntity.ok(response);
    }

    // ユーザーが投稿したレビューを取得
    @GetMapping("/reviews")
    public ResponseEntity<ReviewPageResponse> getUserReviews(@AuthenticationPrincipal User user,
            @ParameterObject @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"updatedAt", "id"},
                    direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String bookId) {
        ReviewPageResponse response = reviewService.getUserReviews(user, pageable, bookId);
        return ResponseEntity.ok(response);
    }

    // ユーザーが投稿したレビューを取得（カーソルベース）
    @GetMapping("/reviews/cursor")
    public ResponseEntity<ReviewCursorResponse> getUserReviewsWithCursor(
            @AuthenticationPrincipal User user, @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE_STR) Integer limit) {
        ReviewCursorResponse response = reviewService.getUserReviewsWithCursor(user, cursor, limit);
        return ResponseEntity.ok(response);
    }

    // ユーザーが追加したお気に入りを取得
    @GetMapping("/favorites")
    public ResponseEntity<FavoritePageResponse> getUserFavorites(@AuthenticationPrincipal User user,
            @ParameterObject @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"updatedAt", "id"},
                    direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String bookId) {
        FavoritePageResponse response = favoriteService.getUserFavorites(user, pageable, bookId);
        return ResponseEntity.ok(response);
    }

    // ユーザーが追加したお気に入りを取得（カーソルベース）
    @GetMapping("/favorites/cursor")
    public ResponseEntity<FavoriteCursorResponse> getUserFavoritesWithCursor(
            @AuthenticationPrincipal User user, @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE_STR) Integer limit) {
        FavoriteCursorResponse response =
                favoriteService.getUserFavoritesWithCursor(user, cursor, limit);
        return ResponseEntity.ok(response);
    }

    // ユーザーが追加したブックマークを取得
    @GetMapping("/bookmarks")
    public ResponseEntity<BookmarkPageResponse> getUserBookmarks(@AuthenticationPrincipal User user,
            @ParameterObject @PageableDefault(size = DEFAULT_PAGE_SIZE, sort = {"updatedAt", "id"},
                    direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String bookId) {
        BookmarkPageResponse responses = bookmarkService.getUserBookmarks(user, pageable, bookId);
        return ResponseEntity.ok(responses);
    }

    // ユーザーが追加したブックマークを取得（カーソルベース）
    @GetMapping("/bookmarks/cursor")
    public ResponseEntity<BookmarkCursorResponse> getUserBookmarksWithCursor(
            @AuthenticationPrincipal User user, @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = DEFAULT_PAGE_SIZE_STR) Integer limit) {
        BookmarkCursorResponse response =
                bookmarkService.getUserBookmarksWithCursor(user, cursor, limit);
        return ResponseEntity.ok(response);
    }

    // ユーザーのプロフィール情報を更新
    @PutMapping("/profile")
    public ResponseEntity<Void> updateUserProfile(
            @Valid @RequestBody UpdateUserProfileRequest request,
            @AuthenticationPrincipal User user) {
        userService.updateUserProfile(request, user);
        return ResponseEntity.noContent().build();
    }

    // ユーザーのメールアドレスを更新
    @PutMapping("/email")
    public ResponseEntity<Void> updateUserEmail(@Valid @RequestBody UpdateUserEmailRequest request,
            @AuthenticationPrincipal User user) {
        userService.updateUserEmail(request, user);
        return ResponseEntity.noContent().build();
    }

    // ユーザーのパスワードを更新
    @PutMapping("/password")
    public ResponseEntity<Void> updateUserPassword(
            @Valid @RequestBody UpdateUserPasswordRequest request,
            @AuthenticationPrincipal User user) {
        userService.updateUserPassword(request, user);
        return ResponseEntity.noContent().build();
    }
}
