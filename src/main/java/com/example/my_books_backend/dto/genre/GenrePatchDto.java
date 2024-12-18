package com.example.my_books_backend.dto.genre;

import org.hibernate.validator.constraints.Length;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenrePatchDto {
    @Length(max = 50)
    private String name;

    @Length(max = 255)
    private String description;
}
