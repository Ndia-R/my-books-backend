package com.example.my_books_backend.dto.book;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCursorResponse {
    private boolean hasNext;
    private String endCursor;
    private List<BookResponse> books;
}
