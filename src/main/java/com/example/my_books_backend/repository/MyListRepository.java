package com.example.my_books_backend.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import com.example.my_books_backend.entity.MyList;
import com.example.my_books_backend.entity.MyListId;

@Repository
public interface MyListRepository extends JpaRepository<MyList, MyListId> {
    Page<MyList> findByUserId(Long userId, Pageable pageable);

    void deleteById(@NonNull MyListId myListId);

    Integer countByUserId(Long userId);

    Integer countByBookId(String bookId);

    Integer countByUserIdAndBookId(Long userId, String bookId);

    List<MyList> findByBookId(String bookId);
}
