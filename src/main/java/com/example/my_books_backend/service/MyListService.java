package com.example.my_books_backend.service;

import com.example.my_books_backend.dto.book.PaginatedBookResponse;
import com.example.my_books_backend.dto.my_list.MyListRequest;
import com.example.my_books_backend.dto.my_list.MyListResponse;
import com.example.my_books_backend.dto.my_list.MyListStateResponse;

public interface MyListService {
    PaginatedBookResponse getMyLists(Integer page, Integer maxResults);

    MyListResponse addMyList(MyListRequest request);

    void removeMyList(String bookId);

    MyListStateResponse getMyListState(String bookId);
}
