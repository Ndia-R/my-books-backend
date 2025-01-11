package com.example.my_books_backend.mapper;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.dto.my_list.CreateMyListRequest;
import com.example.my_books_backend.dto.my_list.MyListResponse;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.MyList;
import com.example.my_books_backend.entity.User;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.repository.BookRepository;
import com.example.my_books_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MyListMapper {
    private final ModelMapper modelMapper;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public MyListResponse toMyListResponse(MyList myList) {
        MyListResponse myListResponse = modelMapper.map(myList, MyListResponse.class);
        User user = modelMapper.map(myList.getUser(), User.class);
        Book book = modelMapper.map(myList.getBook(), Book.class);
        myListResponse.setUser(userMapper.toSimpleUserInfo(user));
        myListResponse.setBook(bookMapper.toBookResponse(book));
        return myListResponse;
    }

    public List<MyListResponse> toMyListResponseList(List<MyList> myLists) {
        return myLists.stream().map(myList -> toMyListResponse(myList)).toList();
    }

    public MyList toMyListEntity(CreateMyListRequest createMyListRequest) {
        MyList myList = new MyList();
        User user = userRepository.findById(createMyListRequest.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        Book book = bookRepository.findById(createMyListRequest.getBookId())
                .orElseThrow(() -> new NotFoundException("Book not found"));
        myList.setUser(user);
        myList.setBook(book);
        return myList;
    }
}
