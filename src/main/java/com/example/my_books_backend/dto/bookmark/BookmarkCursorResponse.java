package com.example.my_books_backend.dto.bookmark;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkCursorResponse {
    private boolean hasNext;
    private Long endCursor;
    private List<BookmarkResponse> bookmarks;
}
