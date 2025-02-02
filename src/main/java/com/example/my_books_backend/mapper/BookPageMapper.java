package com.example.my_books_backend.mapper;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.dto.book_page.BookPageResponse;
import com.example.my_books_backend.entity.BookPage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookPageMapper {
    private final ModelMapper modelMapper;

    public BookPageResponse toBookPageResponse(BookPage bookPage) {
        BookPageResponse bookPageResponse = modelMapper.map(bookPage, BookPageResponse.class);
        return bookPageResponse;
    }

    public List<BookPageResponse> toBookPageResponseList(List<BookPage> bookPages) {
        return bookPages.stream().map(item -> toBookPageResponse(item)).toList();
    }
}
