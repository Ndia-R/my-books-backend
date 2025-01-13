package com.example.my_books_backend.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.my_books_backend.entity.MyList;

@Repository
public interface MyListRepository extends JpaRepository<MyList, Long> {
    List<MyList> findByUserIdOrderByUpdatedAtDesc(Long userId);

    List<MyList> findByBookIdOrderByUpdatedAtDesc(String bookId);

    Integer countByUserIdAndIsDeletedFalse(Long userId);
}
