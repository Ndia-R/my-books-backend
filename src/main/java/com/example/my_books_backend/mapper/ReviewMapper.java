package com.example.my_books_backend.mapper;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.dto.review.CreateReviewRequest;
import com.example.my_books_backend.dto.review.ReviewResponse;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.Review;
import com.example.my_books_backend.entity.User;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.repository.BookRepository;
import com.example.my_books_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReviewMapper {
    private final ModelMapper modelMapper;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public ReviewResponse toReviewResponse(Review review) {
        ReviewResponse reviewResponse = modelMapper.map(review, ReviewResponse.class);
        User user = modelMapper.map(review.getUser(), User.class);
        Book book = modelMapper.map(review.getBook(), Book.class);
        reviewResponse.setUser(userMapper.toSimpleUserInfo(user));
        reviewResponse.setBook(bookMapper.toBookResponse(book));
        return reviewResponse;
    }

    public List<ReviewResponse> toReviewResponseList(List<Review> reviews) {
        return reviews.stream().map(review -> toReviewResponse(review)).toList();
    }

    public Review toReviewEntity(CreateReviewRequest createReviewRequest) {
        Review review = new Review();
        review.setComment(createReviewRequest.getComment());
        review.setRating(createReviewRequest.getRating());
        User user = userRepository.findById(createReviewRequest.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        Book book = bookRepository.findById(createReviewRequest.getBookId())
                .orElseThrow(() -> new NotFoundException("Book not found"));
        review.setUser(user);
        review.setBook(book);
        return review;
    }
}
