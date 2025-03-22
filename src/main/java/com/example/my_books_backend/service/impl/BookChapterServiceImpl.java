package com.example.my_books_backend.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.example.my_books_backend.dto.book_chapter.BookChapterResponse;
import com.example.my_books_backend.dto.book_chapter.BookTableOfContentsResponse;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.BookChapter;
import com.example.my_books_backend.entity.BookContentPage;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.repository.BookChapterRepository;
import com.example.my_books_backend.repository.BookContentPageRepository;
import com.example.my_books_backend.repository.BookRepository;
import com.example.my_books_backend.service.BookChapterService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookChapterServiceImpl implements BookChapterService {
    private final BookChapterRepository bookChapterRepository;
    private final BookRepository bookRepository;
    private final BookContentPageRepository bookContentPageRepository;

    @Override
    public BookTableOfContentsResponse getBookTableOfContents(String bookId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book not found"));

        List<BookChapter> bookChapters = bookChapterRepository.findByBookId(bookId);

        List<BookChapterResponse> bookChapterResponses = bookChapters.stream().map(bookChapter -> {
            Integer chapterNumber = bookChapter.getId().getChapterNumber();

            List<BookContentPage> bookContentPages = bookContentPageRepository
                    .findByIdBookIdAndIdChapterNumber(bookId, chapterNumber);

            List<Integer> pageNumbers = bookContentPages.stream()
                    .map(bookContentPage -> bookContentPage.getId().getPageNumber())
                    .collect(Collectors.toList());

            BookChapterResponse bookChapterResponse = new BookChapterResponse();
            bookChapterResponse.setChapterNumber(chapterNumber);
            bookChapterResponse.setChapterTitle(bookChapter.getTitle());
            bookChapterResponse.setPageNumbers(pageNumbers);

            return bookChapterResponse;
        }).collect(Collectors.toList());

        BookTableOfContentsResponse bookTableOfContentsResponse = new BookTableOfContentsResponse();
        bookTableOfContentsResponse.setBookId(bookId);
        bookTableOfContentsResponse.setTitle(book.getTitle());
        bookTableOfContentsResponse.setChapters(bookChapterResponses);

        return bookTableOfContentsResponse;
    }
}
