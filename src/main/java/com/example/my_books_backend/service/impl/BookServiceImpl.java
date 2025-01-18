package com.example.my_books_backend.service.impl;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.example.my_books_backend.dto.book.BookDetailResponse;
import com.example.my_books_backend.dto.book.BookResponse;
import com.example.my_books_backend.dto.book.PaginatedBookResponse;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.mapper.BookMapper;
import com.example.my_books_backend.repository.BookRepository;
import com.example.my_books_backend.repository.BookRepositoryCustom;
import com.example.my_books_backend.repository.FavoriteRepository;
import com.example.my_books_backend.repository.MyListRepository;
import com.example.my_books_backend.repository.ReviewRepository;
import com.example.my_books_backend.service.BookService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookRepositoryCustom bookRepositoryCustom;
    private final BookMapper bookMapper;

    private final FavoriteRepository favoriteRepository;
    private final MyListRepository myListRepository;
    private final ReviewRepository reviewRepository;

    private static final Integer DEFAULT_START_PAGE = 0;
    private static final Integer DEFAULT_MAX_RESULTS = 20;
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "publishedDate");

    @Override
    public PaginatedBookResponse searchByTitle(String q, Integer page, Integer maxResults) {
        Pageable pageable = createPageable(page, maxResults);
        Page<Book> pageBook = bookRepository.findByTitleContaining(q, pageable);
        return bookMapper.toPaginatedBookResponse(pageBook);
    }

    @Override
    public PaginatedBookResponse searchByGenreId(String genreId, Integer page, Integer maxResults) {
        Pageable pageable = createPageable(page, maxResults);
        Page<Book> pageBook = bookRepositoryCustom.findByGenreIds(genreId, pageable);
        return bookMapper.toPaginatedBookResponse(pageBook);
    }

    @Override
    public List<BookResponse> getNewReleases() {
        List<Book> books = bookRepository.findTop10ByOrderByPublishedDateDesc();
        return bookMapper.toBookResponseList(books);
    }

    @Override
    public BookDetailResponse getBookDetailById(String bookId) {
        Book book = findBookById(bookId);
        BookResponse bookResponse = bookMapper.toBookResponse(book);
        BookDetailResponse bookDetailResponse = bookMapper.toBookDetailResponse(bookResponse);

        Integer favoritesCount = favoriteRepository.countByBookId(bookId);
        Integer myListCount = myListRepository.countByBookId(bookId);
        Integer reviewCount = reviewRepository.countByBookId(bookId);
        Double rating = reviewRepository.findAverageRatingByBookId(bookId);

        if (rating == null) {
            rating = 0.0;
        }

        bookDetailResponse.setFavoriteCount(favoritesCount);
        bookDetailResponse.setMyListCount(myListCount);
        bookDetailResponse.setReviewCount(reviewCount);
        bookDetailResponse.setRating(rating);

        return bookDetailResponse;
    }

    private Pageable createPageable(Integer page, Integer maxResults) {
        page = (page != null) ? page : DEFAULT_START_PAGE;
        maxResults = (maxResults != null) ? maxResults : DEFAULT_MAX_RESULTS;
        return PageRequest.of(page, maxResults, DEFAULT_SORT);
    }

    private Book findBookById(String id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("見つかりませんでした。 ID: " + id));
        return book;
    }
}
