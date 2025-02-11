package com.example.my_books_backend.dto.bookmark;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkPageResponse {
    private Integer page;
    private Integer totalPages;
    private Integer totalItems;
    private List<BookmarkResponse> bookmarks;
}
