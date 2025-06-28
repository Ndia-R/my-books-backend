package com.example.my_books_backend.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileCountsResponse {
    private Integer favoriteCount;
    private Integer bookmarkCount;
    private Integer reviewCount;
}
