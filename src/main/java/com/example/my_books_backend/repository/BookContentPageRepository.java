package com.example.my_books_backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.my_books_backend.entity.BookContentPage;
import com.example.my_books_backend.entity.BookContentPageId;

@Repository
public interface BookContentPageRepository
        extends JpaRepository<BookContentPage, BookContentPageId> {
    List<BookContentPage> findByIdBookIdAndIdChapterNumber(String bookId, Integer chapterNumber);
}
