package com.example.my_books_backend.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.my_books_backend.dto.book_page.BookPageResponse;
import com.example.my_books_backend.entity.BookPage;
import com.example.my_books_backend.mapper.BookPageMapper;
import com.example.my_books_backend.repository.BookPageRepository;
import com.example.my_books_backend.service.BookPageService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookPageServiceImpl implements BookPageService {
    private final BookPageRepository bookPageRepository;
    private final BookPageMapper bookPageMapper;

    @Override
    public List<BookPageResponse> getBookPageByBookId(String bookId) {
        List<BookPage> bookPage = bookPageRepository.findByBookId(bookId);
        return bookPageMapper.toBookPageResponseList(bookPage);
    }
}
