package com.example.my_books_backend.dto.bookmark;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkRequest {
    @NotNull
    private String bookId;

    @NotNull
    @Min(1)
    private Integer chapterNumber;

    @NotNull
    @Min(1)
    private Integer pageNumber;

    @NotNull
    private String note;
}
