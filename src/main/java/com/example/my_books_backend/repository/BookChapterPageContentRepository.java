package com.example.my_books_backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.my_books_backend.entity.BookChapterPageContent;
import com.example.my_books_backend.entity.BookChapterPageContentId;

@Repository
public interface BookChapterPageContentRepository
        extends JpaRepository<BookChapterPageContent, BookChapterPageContentId> {
    List<BookChapterPageContent> findByIdBookIdAndIdChapterNumber(String bookId,
            Integer chapterNumber);
}
