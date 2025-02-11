package com.example.my_books_backend.dto.favorite;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoritePageResponse {
    private Integer page;
    private Integer totalPages;
    private Integer totalItems;
    private List<FavoriteResponse> favorites;
}
