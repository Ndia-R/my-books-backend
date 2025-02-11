package com.example.my_books_backend.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.example.my_books_backend.dto.book.BookResponse;
import com.example.my_books_backend.dto.book.BookPageResponse;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.mapper.BookMapper;
import com.example.my_books_backend.repository.BookRepository;
import com.example.my_books_backend.service.BookService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    private static final Integer DEFAULT_START_PAGE = 0;
    private static final Integer DEFAULT_MAX_RESULTS = 20;
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "publishedDate");

    @Override
    public BookResponse getBookById(String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found"));
        return bookMapper.toBookResponse(book);
    }

    @Override
    public List<BookResponse> getNewBooks() {
        List<Book> books = bookRepository.findTop10ByOrderByPublishedDateDesc();
        return bookMapper.toBookResponseList(books);
    }

    @Override
    public BookPageResponse searchByTitle(String q, Integer page, Integer maxResults) {
        Pageable pageable = createPageable(page, maxResults);
        Page<Book> bookPage = bookRepository.findByTitleContaining(q, pageable);
        return bookMapper.toBookPageResponse(bookPage);
    }

    @Override
    public BookPageResponse searchByGenreId(String genreId, Integer page, Integer maxResults) {
        Pageable pageable = createPageable(page, maxResults);

        Boolean isAndSearch = genreId.contains(",");

        // split()で、"|"を正規表現として解釈されないようにエスケープ
        String splitStr = isAndSearch ? "," : "\\|";

        List<Long> genreIds = Arrays.stream(genreId.split(splitStr)).map(Long::parseLong)
                .collect(Collectors.toList());

        Page<Book> bookPage =
                isAndSearch ? bookRepository.findByAllGenreIds(genreIds, genreIds.size(), pageable)
                        : bookRepository.findByGenreIds(genreIds, pageable);

        return bookMapper.toBookPageResponse(bookPage);
    }

    private Pageable createPageable(Integer page, Integer maxResults) {
        page = (page != null) ? page : DEFAULT_START_PAGE;
        maxResults = (maxResults != null) ? maxResults : DEFAULT_MAX_RESULTS;
        return PageRequest.of(page, maxResults, DEFAULT_SORT);
    }
}
