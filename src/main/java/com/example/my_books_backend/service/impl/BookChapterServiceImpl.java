package com.example.my_books_backend.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.my_books_backend.dto.book_chapter.BookChapterResponse;
import com.example.my_books_backend.entity.BookChapter;
import com.example.my_books_backend.mapper.BookChapterMapper;
import com.example.my_books_backend.repository.BookChapterRepository;
import com.example.my_books_backend.service.BookChapterService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookChapterServiceImpl implements BookChapterService {
    private final BookChapterRepository bookChapterRepository;
    private final BookChapterMapper bookChapterMapper;

    @Override
    public List<BookChapterResponse> getBookChapterByBookId(String bookId) {
        List<BookChapter> bookChapter = bookChapterRepository.findByBookId(bookId);
        return bookChapterMapper.toBookChapterResponseList(bookChapter);
    }
}
