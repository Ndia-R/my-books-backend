package com.example.my_books_backend.dto.review;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCountsResponse {
    private String bookId;
    private Long reviewCount;
    private Double averageRating;
}
