package com.example.my_books_backend.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import com.example.my_books_backend.dto.PageResponse;
import com.example.my_books_backend.dto.SliceResponse;
import com.example.my_books_backend.dto.review.ReviewResponse;
import com.example.my_books_backend.entity.Review;

@Mapper(componentModel = "spring")
public abstract class ReviewMapper {

    @Autowired
    protected BookMapper bookMapper;

    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "name", source = "user.name")
    @Mapping(target = "avatarPath", source = "user.avatarPath")
    @Mapping(target = "book", expression = "java(bookMapper.toBookResponse(review.getBook()))")
    public abstract ReviewResponse toReviewResponse(Review review);

    public abstract List<ReviewResponse> toReviewResponseList(List<Review> reviews);

    public PageResponse<ReviewResponse> toPageResponse(Page<Review> reviews) {
        List<ReviewResponse> responses = toReviewResponseList(reviews.getContent());
        // Pageableの内部的にはデフォルトで0ベースだが、エンドポイントとしては1ベースなので+1する
        return new PageResponse<ReviewResponse>(
            reviews.getNumber() + 1,
            reviews.getSize(),
            reviews.getTotalPages(),
            reviews.getTotalElements(),
            reviews.hasNext(),
            reviews.hasPrevious(),
            responses
        );
    }

    public SliceResponse<ReviewResponse> toSliceResponse(Slice<Review> reviews) {
        List<ReviewResponse> responses = toReviewResponseList(reviews.getContent());
        // Pageableの内部的にはデフォルトで0ベースだが、エンドポイントとしては1ベースなので+1する
        return new SliceResponse<ReviewResponse>(
            reviews.getNumber() + 1,
            reviews.getSize(),
            reviews.hasNext(),
            responses
        );
    }
}
