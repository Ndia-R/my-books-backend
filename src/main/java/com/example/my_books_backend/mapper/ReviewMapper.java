package com.example.my_books_backend.mapper;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.dto.review.ReviewPageResponse;
import com.example.my_books_backend.dto.review.ReviewResponse;
import com.example.my_books_backend.entity.Review;
import com.example.my_books_backend.entity.User;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReviewMapper {
    private final ModelMapper modelMapper;
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

    public ReviewPageResponse toReviewPageResponse(Page<Review> reviewPage) {
        Integer page = reviewPage.getNumber();
        Integer totalPages = reviewPage.getTotalPages();
        Integer totalItems = (int) reviewPage.getTotalElements();
        List<ReviewResponse> reviewResponses = toReviewResponseList(reviewPage.getContent());
        return new ReviewPageResponse(page, totalPages, totalItems, reviewResponses);
    }
}
