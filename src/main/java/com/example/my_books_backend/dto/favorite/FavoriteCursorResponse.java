package com.example.my_books_backend.dto.favorite;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteCursorResponse {
    private boolean hasNext;
    private Long endCursor;
    private List<FavoriteResponse> favorites;
}

