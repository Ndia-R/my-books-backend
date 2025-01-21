package com.example.my_books_backend.mapper;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.dto.review.MyReviewResponse;
import com.example.my_books_backend.dto.review.PaginatedMyReviewResponse;
import com.example.my_books_backend.dto.review.PaginatedReviewResponse;
import com.example.my_books_backend.dto.review.ReviewResponse;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.Review;
import com.example.my_books_backend.entity.User;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReviewMapper {
    private final ModelMapper modelMapper;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;

    public ReviewResponse toReviewResponse(Review review) {
        ReviewResponse reviewResponse = modelMapper.map(review, ReviewResponse.class);
        User user = modelMapper.map(review.getUser(), User.class);
        reviewResponse.setUser(userMapper.toSimpleUserInfo(user));
        return reviewResponse;
    }

    public List<ReviewResponse> toReviewResponseList(List<Review> reviews) {
        return reviews.stream().map(review -> toReviewResponse(review)).toList();
    }

    public PaginatedReviewResponse toPaginatedReviewResponse(Page<Review> reviews) {
        Integer page = reviews.getNumber();
        Integer totalPages = reviews.getTotalPages();
        Integer totalItems = (int) reviews.getTotalElements();
        List<ReviewResponse> reviewResponseList = toReviewResponseList(reviews.getContent());
        return new PaginatedReviewResponse(page, totalPages, totalItems, reviewResponseList);
    }

    public MyReviewResponse toMyReviewResponse(Review review) {
        MyReviewResponse myReviewResponse = modelMapper.map(review, MyReviewResponse.class);
        User user = modelMapper.map(review.getUser(), User.class);
        Book book = modelMapper.map(review.getBook(), Book.class);
        myReviewResponse.setUser(userMapper.toSimpleUserInfo(user));
        myReviewResponse.setBook(bookMapper.toBookResponse(book));
        return myReviewResponse;
    }

    public List<MyReviewResponse> toMyReviewResponseList(List<Review> reviews) {
        return reviews.stream().map(review -> toMyReviewResponse(review)).toList();
    }

    public PaginatedMyReviewResponse toPaginatedMyReviewResponse(Page<Review> reviews) {
        Integer page = reviews.getNumber();
        Integer totalPages = reviews.getTotalPages();
        Integer totalItems = (int) reviews.getTotalElements();
        List<MyReviewResponse> myReviewResponseList = toMyReviewResponseList(reviews.getContent());
        return new PaginatedMyReviewResponse(page, totalPages, totalItems, myReviewResponseList);
    }
}
