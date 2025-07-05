package com.example.my_books_backend.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SliceResponse<T> {
    private int currentPage; // ページ番号は1ベース
    private int pageSize;
    private boolean hasNext;
    private List<T> data;
}
