package com.example.my_books_backend.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private int currentPage; // ページ番号は1ベース
    private int pageSize;
    private int totalPages;
    private long totalItems;
    private boolean hasNext;
    private boolean hasPrevious;
    private List<T> data;
}
