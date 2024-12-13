package com.example.my_books_backend.service.impl;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.example.my_books_backend.dto.book.BookDto;
import com.example.my_books_backend.dto.book.BookResponseDto;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.mapper.BookMapper;
import com.example.my_books_backend.model.Book;
import com.example.my_books_backend.repository.BookRepository;
import com.example.my_books_backend.repository.BookRepositoryCustom;
import com.example.my_books_backend.service.BookService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookRepositoryCustom bookRepositoryCustom;
    private final BookMapper bookMapper;

    private static final Integer DEFAULT_START_PAGE = 0;
    private static final Integer DEFAULT_MAX_RESULTS = 20;
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "publishedDate");

    @Override
    public List<BookDto> getBooks() {
        List<Book> books = bookRepository.findAll();
        List<BookDto> booksDto = bookMapper.toDtoList(books);
        return booksDto;
    }

    @Override
    public BookDto getBookById(String id) {
        Book book = findBookById(id);
        BookDto bookDto = bookMapper.toDto(book);
        return bookDto;
    }

    @Override
    public BookResponseDto searchByTitle(String q, Integer page, Integer maxResults) {
        Pageable pageable = createPageable(page, maxResults);
        Page<Book> pageBook = bookRepository.findByTitleContaining(q, pageable);
        BookResponseDto bookResponseDto = bookMapper.toResponseDto(pageBook);
        return bookResponseDto;
    }

    @Override
    public BookResponseDto searchByGenreId(String genreId, Integer page, Integer maxResults) {
        Pageable pageable = createPageable(page, maxResults);
        Page<Book> pageBook = bookRepositoryCustom.findByGenreIds(genreId, pageable);
        BookResponseDto bookResponseDto = bookMapper.toResponseDto(pageBook);
        return bookResponseDto;
    }

    @Override
    public List<BookDto> getNewReleases() {
        List<Book> books = bookRepository.findTop10ByOrderByPublishedDateDesc();
        List<BookDto> booksDto = bookMapper.toDtoList(books);
        return booksDto;
    }

    private Pageable createPageable(Integer page, Integer maxResults) {
        page = (page != null) ? page : DEFAULT_START_PAGE;
        maxResults = (maxResults != null) ? maxResults : DEFAULT_MAX_RESULTS;
        return PageRequest.of(page, maxResults, DEFAULT_SORT);
    }

    private Book findBookById(String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found with this ID: " + id));
        return book;
    }
}
