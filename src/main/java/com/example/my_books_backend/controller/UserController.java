package com.example.my_books_backend.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.my_books_backend.dto.user.UserProfileCountsResponse;
import com.example.my_books_backend.dto.user.UserProfileResponse;
import com.example.my_books_backend.dto.bookmark.BookmarkPageResponse;
import com.example.my_books_backend.dto.bookmark.BookmarkResponse;
import com.example.my_books_backend.dto.favorite.FavoritePageResponse;
import com.example.my_books_backend.dto.favorite.FavoriteResponse;
import com.example.my_books_backend.dto.review.ReviewPageResponse;
import com.example.my_books_backend.dto.review.ReviewResponse;
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

    // 自分のプロフィール情報
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(@AuthenticationPrincipal User user) {
        UserProfileResponse response = userService.getUserProfile(user);
        return ResponseEntity.ok(response);
    }

    // 自分のレビュー、お気に入り、ブックマークの数
    @GetMapping("/profile-counts")
    public ResponseEntity<UserProfileCountsResponse> getUserProfileCounts(
            @AuthenticationPrincipal User user) {
        UserProfileCountsResponse response = userService.getUserProfileCounts(user);
        return ResponseEntity.ok(response);
    }

    // 自分のレビュー一覧
    @GetMapping("/reviews")
    public ResponseEntity<ReviewPageResponse> getUserReviews(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer maxResults,
            @AuthenticationPrincipal User user) {
        ReviewPageResponse response = reviewService.getUserReviews(page, maxResults, user);
        return ResponseEntity.ok(response);
    }

    // 自分のお気に入り一覧
    @GetMapping("/favorites")
    public ResponseEntity<FavoritePageResponse> getUserFavorites(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer maxResults,
            @AuthenticationPrincipal User user) {
        FavoritePageResponse response = favoriteService.getUserFavorites(page, maxResults, user);
        return ResponseEntity.ok(response);
    }

    // 自分のブックマーク一覧
    @GetMapping("/bookmarks")
    public ResponseEntity<BookmarkPageResponse> getUserBookmarks(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer maxResults,
            @AuthenticationPrincipal User user) {
        BookmarkPageResponse responses = bookmarkService.getUserBookmarks(page, maxResults, user);
        return ResponseEntity.ok(responses);
    }

    // 自分が投稿した特定の書籍のレビュー
    // （書籍１冊に対して、１つのレビューなので「単数形」）
    @GetMapping("/books/{bookId}/review")
    public ResponseEntity<ReviewResponse> getUserReviewForBook(@PathVariable String bookId,
            @AuthenticationPrincipal User user) {
        ReviewResponse response = reviewService.getUserReviewForBook(bookId, user);
        return ResponseEntity.ok(response);
    }

    // 自分の追加した特定の書籍のお気に入り
    // （書籍１冊に対して、１つのお気に入りなので「単数形」）
    @GetMapping("/books/{bookId}/favorite")
    public ResponseEntity<FavoriteResponse> getUserFavoriteForBook(@PathVariable String bookId,
            @AuthenticationPrincipal User user) {
        FavoriteResponse response = favoriteService.getUserFavoriteForBook(bookId, user);
        return ResponseEntity.ok(response);
    }

    // 自分の追加した特定の書籍のブックマークリスト
    // （書籍１冊に対して、複数のブックマークなので「複数形」）
    @GetMapping("/books/{bookId}/bookmarks")
    public ResponseEntity<List<BookmarkResponse>> getUserBookmarksForBook(
            @PathVariable String bookId, @AuthenticationPrincipal User user) {
        List<BookmarkResponse> responses = bookmarkService.getUserBookmarksForBook(bookId, user);
        return ResponseEntity.ok(responses);
    }

    // 自分のプロフィール情報を更新
    @PutMapping("/profile")
    public ResponseEntity<Void> updateUserProfile(
            @Valid @RequestBody UpdateUserProfileRequest request,
            @AuthenticationPrincipal User user) {
        userService.updateUserProfile(request, user);
        return ResponseEntity.noContent().build();
    }

    // 自分のメールアドレスを更新
    @PutMapping("/email")
    public ResponseEntity<Void> updateUserEmail(@Valid @RequestBody UpdateUserEmailRequest request,
            @AuthenticationPrincipal User user) {
        userService.updateUserEmail(request, user);
        return ResponseEntity.noContent().build();
    }

    // 自分のパスワードを更新
    @PutMapping("/password")
    public ResponseEntity<Void> updateUserPassword(
            @Valid @RequestBody UpdateUserPasswordRequest request,
            @AuthenticationPrincipal User user) {
        userService.updateUserPassword(request, user);
        return ResponseEntity.noContent().build();
    }
}
