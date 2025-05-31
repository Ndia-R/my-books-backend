package com.example.my_books_backend.mapper;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.dto.review.ReviewPageResponse;
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

    public ReviewResponse toReviewResponse(Review review) {
        ReviewResponse response = modelMapper.map(review, ReviewResponse.class);
        User user = modelMapper.map(review.getUser(), User.class);
        Book book = modelMapper.map(review.getBook(), Book.class);
        response.setName(user.getName());
        response.setAvatarPath(user.getAvatarPath());
        response.setBook(bookMapper.toBookResponse(book));
        return response;
    }

    public List<ReviewResponse> toReviewResponseList(List<Review> reviews) {
        return reviews.stream().map(review -> toReviewResponse(review)).toList();
    }

    public ReviewPageResponse toReviewPageResponse(Page<Review> reviews) {
        Integer page = reviews.getNumber();
        Integer totalPages = reviews.getTotalPages();
        Integer totalItems = (int) reviews.getTotalElements();
        List<ReviewResponse> responses = toReviewResponseList(reviews.getContent());
        return new ReviewPageResponse(page, totalPages, totalItems, responses);
    }
}
