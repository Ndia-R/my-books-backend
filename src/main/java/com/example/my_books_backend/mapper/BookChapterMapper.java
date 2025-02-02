package com.example.my_books_backend.mapper;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.dto.book_chapter.BookChapterResponse;
import com.example.my_books_backend.entity.BookChapter;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookChapterMapper {
    private final ModelMapper modelMapper;

    public BookChapterResponse toBookChapterResponse(BookChapter bookChapter) {
        BookChapterResponse bookChapterResponse =
                modelMapper.map(bookChapter, BookChapterResponse.class);
        return bookChapterResponse;
    }

    public List<BookChapterResponse> toBookChapterResponseList(List<BookChapter> bookChapters) {
        return bookChapters.stream().map(item -> toBookChapterResponse(item)).toList();
    }
}
