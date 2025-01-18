package com.example.my_books_backend.mapper;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.dto.book.BookResponse;
import com.example.my_books_backend.dto.book.PaginatedBookResponse;
import com.example.my_books_backend.dto.my_list.MyListResponse;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.MyList;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MyListMapper {
    private final ModelMapper modelMapper;
    private final BookMapper bookMapper;
    private final UserMapper userMapper;

    public MyListResponse toMyListResponse(MyList myList) {
        MyListResponse myListResponse = modelMapper.map(myList, MyListResponse.class);
        myListResponse.setUser(userMapper.toSimpleUserInfo(myList.getUser()));
        myListResponse.setBook(bookMapper.toBookResponse(myList.getBook()));
        return myListResponse;
    }

    public List<MyListResponse> toMyListResponseList(List<MyList> myLists) {
        return myLists.stream().map(item -> toMyListResponse(item)).toList();
    }

    public PaginatedBookResponse toPaginatedBookResponse(Page<MyList> myLists) {
        Integer page = myLists.getNumber();
        Integer totalPages = myLists.getTotalPages();
        Integer totalItems = (int) myLists.getTotalElements();
        List<Book> books = myLists.getContent().stream().map(item -> item.getBook()).toList();
        List<BookResponse> booksDto = bookMapper.toBookResponseList(books);
        return new PaginatedBookResponse(page, totalPages, totalItems, booksDto);
    }
}
