package com.example.my_books_backend.controller;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.example.my_books_backend.dto.book.PaginatedBookResponse;
import com.example.my_books_backend.dto.my_list.MyListRequest;
import com.example.my_books_backend.dto.my_list.MyListResponse;
import com.example.my_books_backend.dto.my_list.MyListInfoResponse;
import com.example.my_books_backend.service.MyListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/my-lists")
@RequiredArgsConstructor
public class MyListController {
    private final MyListService myListService;

    @GetMapping("/{bookId}/info")
    public ResponseEntity<MyListInfoResponse> getMyListInfo(@PathVariable String bookId) {
        MyListInfoResponse myListInfoResponse = myListService.getMyListInfo(bookId);
        return ResponseEntity.ok(myListInfoResponse);
    }

    @GetMapping("")
    public ResponseEntity<PaginatedBookResponse> getMyLists(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer maxResults) {
        PaginatedBookResponse mylists = myListService.getMyLists(page, maxResults);
        return ResponseEntity.ok(mylists);
    }

    @PostMapping("")
    public ResponseEntity<MyListResponse> addMyList(@Valid @RequestBody MyListRequest request) {
        MyListResponse mylist = myListService.addMyList(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(mylist.getMyListId()).toUri();
        return ResponseEntity.created(location).body(mylist);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> removeMyList(@PathVariable String bookId) {
        myListService.removeMyList(bookId);
        return ResponseEntity.noContent().build();
    }
}
