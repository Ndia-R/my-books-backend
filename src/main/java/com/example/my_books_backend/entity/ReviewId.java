package com.example.my_books_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ReviewId implements Serializable {
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "book_id")
    private String bookId;
}
