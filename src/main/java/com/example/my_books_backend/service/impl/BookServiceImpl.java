package com.example.my_books_backend.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.example.my_books_backend.dto.genre.GenreResponse;
import com.example.my_books_backend.dto.book.BookDetailsResponse;
import com.example.my_books_backend.dto.book.BookPageResponse;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.Genre;
import com.example.my_books_backend.exception.BadRequestException;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.mapper.BookMapper;
import com.example.my_books_backend.repository.BookRepository;
import com.example.my_books_backend.service.BookService;
import com.example.my_books_backend.service.GenreService;
import com.example.my_books_backend.util.PaginationUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
        private final BookRepository bookRepository;
        private final BookMapper bookMapper;

        private final PaginationUtil paginationUtil;
        private final GenreService genreService;

        private static final Sort DEFAULT_SORT =
                        Sort.by(Sort.Order.desc("publishedDate"), Sort.Order.asc("id"));

        @Override
        public BookDetailsResponse getBookDetailsById(String id) {
                Book book = bookRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException("Book not found"));

                List<GenreResponse> genreResponses = genreService.getAllGenres();

                List<Long> bookGenreIds = book.getGenres().stream().map(Genre::getId)
                                .collect(Collectors.toList());

                List<GenreResponse> filteredGenres = genreResponses.stream().filter(
                                genreResponse -> bookGenreIds.contains(genreResponse.getId()))
                                .collect(Collectors.toList());

                BookDetailsResponse bookDetailsResponse = bookMapper.toBookDetailsResponse(book);

                bookDetailsResponse.setGenres(filteredGenres);

                return bookDetailsResponse;
        }

        @Override
        public BookPageResponse getNewBooks(Integer page, Integer maxResults) {
                Pageable pageable = paginationUtil.createPageable(page, maxResults, DEFAULT_SORT);
                Page<Book> bookPage = bookRepository.findTop10ByOrderByPublishedDateDesc(pageable);
                return bookMapper.toBookPageResponse(bookPage);
        }

        @Override
        public BookPageResponse getBookPageByTitle(String query, Integer page, Integer maxResults) {
                Pageable pageable = paginationUtil.createPageable(page, maxResults, DEFAULT_SORT);
                Page<Book> bookPage = bookRepository.findByTitleContaining(query, pageable);
                return bookMapper.toBookPageResponse(bookPage);
        }

        @Override
        public BookPageResponse getBookPageByGenreId(String genreIdsQuery, String conditionQuery,
                        Integer page, Integer maxResults) {
                if (!(conditionQuery.equals("SINGLE") || conditionQuery.equals("AND")
                                || conditionQuery.equals("OR"))) {
                        throw new BadRequestException("検索条件が不正です。");
                }

                Pageable pageable = paginationUtil.createPageable(page, maxResults, DEFAULT_SORT);

                List<Long> genreIds = Arrays.stream(genreIdsQuery.split(",")).map(Long::parseLong)
                                .collect(Collectors.toList());

                Boolean isAndSearch = conditionQuery.equals("AND");

                Page<Book> bookPage = isAndSearch
                                ? bookRepository.findByAllGenreIds(genreIds, genreIds.size(),
                                                pageable)
                                : bookRepository.findByGenreIds(genreIds, pageable);

                return bookMapper.toBookPageResponse(bookPage);
        }
}
