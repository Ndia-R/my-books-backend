package com.example.my_books_backend.repository.favorite;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.Favorite;
import com.example.my_books_backend.entity.User;

@Repository
public interface FavoriteRepository
        extends JpaRepository<Favorite, Long>, FavoriteRepositoryCustom {
    // ユーザーが追加したお気に入りを取得
    Page<Favorite> findByUserAndIsDeletedFalse(User user, Pageable pageable);

    // ユーザーが追加したお気に入りを取得（書籍ID指定）
    Page<Favorite> findByUserAndIsDeletedFalseAndBookId(User user, Pageable pageable,
            String bookId);

    List<Favorite> findByUserAndBookIdAndIsDeletedFalse(User user, String bookId);

    Optional<Favorite> findByUserAndBook(User user, Book book);

    List<Favorite> findByBookId(String bookId);

    Integer countByUserIdAndIsDeletedFalse(Long userId);
}
