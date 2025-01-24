package com.example.my_books_backend.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.my_books_backend.dto.book.PaginatedBookResponse;
import com.example.my_books_backend.dto.my_list.MyListCountResponse;
import com.example.my_books_backend.dto.my_list.MyListRequest;
import com.example.my_books_backend.dto.my_list.MyListResponse;
import com.example.my_books_backend.dto.my_list.MyListStatusResponse;
import com.example.my_books_backend.dto.my_list.MyListInfoResponse;
import com.example.my_books_backend.entity.Book;
import com.example.my_books_backend.entity.MyList;
import com.example.my_books_backend.entity.MyListId;
import com.example.my_books_backend.entity.User;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.mapper.MyListMapper;
import com.example.my_books_backend.repository.BookRepository;
import com.example.my_books_backend.repository.MyListRepository;
import com.example.my_books_backend.service.MyListService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MyListServiceImpl implements MyListService {
    private final MyListRepository myListRepository;
    private final MyListMapper myListMapper;
    private final BookRepository bookRepository;

    private static final Integer DEFAULT_START_PAGE = 0;
    private static final Integer DEFAULT_MAX_RESULTS = 20;
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "updatedAt");

    @Override
    public PaginatedBookResponse getMyLists(Integer page, Integer maxResults) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Pageable pageable = createPageable(page, maxResults);
        Page<MyList> myLists = myListRepository.findByUserId(user.getId(), pageable);
        return myListMapper.toPaginatedBookResponse(myLists);
    }

    @Override
    @Transactional
    public MyListResponse addMyList(MyListRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new NotFoundException("Book not found"));
        MyListId myListId = new MyListId(user.getId(), request.getBookId());
        MyList myList = new MyList();
        myList.setId(myListId);
        myList.setUser(user);
        myList.setBook(book);
        MyList savedMyList = myListRepository.save(myList);
        return myListMapper.toMyListResponse(savedMyList);
    }

    @Override
    @Transactional
    public void removeMyList(String bookId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        MyListId myListId = new MyListId(user.getId(), bookId);
        myListRepository.deleteById(myListId);
    }

    @Override
    public MyListStatusResponse getMyListStatus(String bookId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        Integer count = myListRepository.countByUserIdAndBookId(user.getId(), bookId);

        MyListStatusResponse myListStateResponse = new MyListStatusResponse();
        myListStateResponse.setBookId(bookId);
        myListStateResponse.setIsMyList(count > 0 ? true : false);
        return myListStateResponse;
    }

    @Override
    public MyListCountResponse getMyListCount(String bookId) {
        Integer myListCount = myListRepository.countByBookId(bookId);

        MyListCountResponse myListCountResponse = new MyListCountResponse();
        myListCountResponse.setBookId(bookId);
        myListCountResponse.setMyListCount(myListCount);
        return myListCountResponse;
    }

    @Override
    public MyListInfoResponse getMyListInfo(String bookId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Integer count = 0;
        // 認証済みであればユーザー情報取得（匿名ユーザーは未認証とする）
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            User user = (User) authentication.getPrincipal();
            count = myListRepository.countByUserIdAndBookId(user.getId(), bookId);
        }
        Integer myListCount = myListRepository.countByBookId(bookId);

        MyListInfoResponse myListInfoResponse = new MyListInfoResponse();
        myListInfoResponse.setBookId(bookId);
        myListInfoResponse.setIsMyList(count > 0 ? true : false);
        myListInfoResponse.setMyListCount(myListCount);
        return myListInfoResponse;
    }

    private Pageable createPageable(Integer page, Integer maxResults) {
        page = (page != null) ? page : DEFAULT_START_PAGE;
        maxResults = (maxResults != null) ? maxResults : DEFAULT_MAX_RESULTS;
        return PageRequest.of(page, maxResults, DEFAULT_SORT);
    }
}
