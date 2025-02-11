package com.example.my_books_backend.service.impl;

import org.springframework.stereotype.Service;
import com.example.my_books_backend.dto.book_content_page.BookContentPageResponse;
import com.example.my_books_backend.entity.BookChapter;
import com.example.my_books_backend.entity.BookChapterId;
import com.example.my_books_backend.entity.BookContentPage;
import com.example.my_books_backend.entity.BookContentPageId;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.repository.BookChapterRepository;
import com.example.my_books_backend.repository.BookContentPageRepository;
import com.example.my_books_backend.service.BookContentPageService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookContentPageServiceImpl implements BookContentPageService {
    private final BookChapterRepository bookChapterRepository;
    private final BookContentPageRepository bookContentPageRepository;

    @Override
    public BookContentPageResponse getBookContentPage(String bookId, Integer chapterNumber,
            Integer pageNumber) {
        BookContentPageId bookContentPageId =
                new BookContentPageId(bookId, chapterNumber, pageNumber);
        BookChapterId bookChapterId = new BookChapterId(bookId, chapterNumber);

        BookContentPage bookContentPage = bookContentPageRepository.findById(bookContentPageId)
                .orElseThrow(() -> new NotFoundException("BookContentPage not found"));
        BookChapter bookChapter = bookChapterRepository.findById(bookChapterId)
                .orElseThrow(() -> new NotFoundException("BookChapter not found"));

        BookContentPageResponse bookContentPageResponse = new BookContentPageResponse();
        bookContentPageResponse.setBookId(bookId);
        bookContentPageResponse.setChapterNumber(chapterNumber);
        bookContentPageResponse.setChapterTitle(bookChapter.getTitle());
        bookContentPageResponse.setPageNumber(pageNumber);
        bookContentPageResponse.setContent(bookContentPage.getContent());

        return bookContentPageResponse;
    }
}
