package com.example.my_books_backend.dto.genre;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreDto {
    private Integer id;
    private String name;
}
