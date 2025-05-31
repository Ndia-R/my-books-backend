package com.example.my_books_backend.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CursorPageResponse<T> {
    private String endCursor;
    private boolean hasNext;
    private List<T> data;
}
