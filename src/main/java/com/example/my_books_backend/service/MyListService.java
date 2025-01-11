package com.example.my_books_backend.service;

import java.util.List;
import com.example.my_books_backend.dto.my_list.CreateMyListRequest;
import com.example.my_books_backend.dto.my_list.MyListResponse;

public interface MyListService {
    List<MyListResponse> getAllMyLists();

    MyListResponse getMyListById(Long id);

    MyListResponse createMyList(CreateMyListRequest request);

    void deleteMyList(Long id);

    List<MyListResponse> getMyListsByUserId(Long userId);

    List<MyListResponse> getMyListsByBookId(String bookId);
}
