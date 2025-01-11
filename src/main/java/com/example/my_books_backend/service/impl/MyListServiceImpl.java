package com.example.my_books_backend.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import com.example.my_books_backend.dto.my_list.CreateMyListRequest;
import com.example.my_books_backend.dto.my_list.MyListResponse;
import com.example.my_books_backend.entity.MyList;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.mapper.MyListMapper;
import com.example.my_books_backend.repository.MyListRepository;
import com.example.my_books_backend.service.MyListService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyListServiceImpl implements MyListService {
    private final MyListRepository myListRepository;
    private final MyListMapper myListMapper;

    @Override
    public List<MyListResponse> getAllMyLists() {
        List<MyList> myLists = myListRepository.findAll();
        return myListMapper.toMyListResponseList(myLists);
    }

    @Override
    public MyListResponse getMyListById(Long id) {
        MyList myList = findMyListById(id);
        return myListMapper.toMyListResponse(myList);
    }

    @Override
    public MyListResponse createMyList(CreateMyListRequest request) {
        MyList myList = myListMapper.toMyListEntity(request);
        MyList savedMyList = myListRepository.save(myList);
        return myListMapper.toMyListResponse(savedMyList);
    }

    @Override
    public void deleteMyList(Long id) {
        MyList myList = findMyListById(id);
        myListRepository.delete(myList);
    }

    @Override
    public List<MyListResponse> getMyListsByUserId(Long userId) {
        List<MyList> myLists = myListRepository.findByUserIdOrderByUpdatedAtDesc(userId);
        return myListMapper.toMyListResponseList(myLists);
    }

    @Override
    public List<MyListResponse> getMyListsByBookId(String bookId) {
        List<MyList> myLists = myListRepository.findByBookIdOrderByUpdatedAtDesc(bookId);
        return myListMapper.toMyListResponseList(myLists);
    }

    private MyList findMyListById(Long id) {
        MyList myList = myListRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("見つかりませんでした。 ID: " + id));
        return myList;
    }
}
