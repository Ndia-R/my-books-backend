package com.example.my_books_backend.dto.review;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCursorResponse {
    private boolean hasNext;
    private Long endCursor;
    private List<ReviewResponse> reviews;
}

