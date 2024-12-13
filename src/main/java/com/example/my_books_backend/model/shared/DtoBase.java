package com.example.my_books_backend.model.shared;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DtoBase {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
