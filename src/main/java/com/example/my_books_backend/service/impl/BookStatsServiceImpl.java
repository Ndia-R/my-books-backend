package com.example.my_books_backend.service.impl;

import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.Review;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.repository.BookRepository;
import com.example.my_books_backend.repository.ReviewRepository;
import com.example.my_books_backend.service.BookStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookStatsServiceImpl implements BookStatsService {

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;

    /**
     * {@inheritDoc}
     */
    @Transactional
    public void updateBookStats(String bookId) {
        List<Review> reviews = reviewRepository.findByBookIdAndIsDeletedFalse(bookId);

        int reviewCount = reviews.size();
        double averageRating = reviews.stream().mapToDouble(Review::getRating).average().orElse(0.0);
        double popularity = reviewCount == 0 ? 0.0 : (reviewCount * averageRating) / (reviewCount + 10);

        Book book = bookRepository.findById(bookId)
            .orElseThrow(() -> new NotFoundException("Book not found"));

        book.setReviewCount(reviewCount);
        book.setAverageRating(Math.round(averageRating * 100.0) / 100.0);
        book.setPopularity(Math.round(popularity * 1000.0) / 1000.0);

        bookRepository.save(book);
    }
}
