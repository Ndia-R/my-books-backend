package com.example.my_books_backend.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.example.my_books_backend.dto.my_list.CreateMyListRequest;
import com.example.my_books_backend.dto.my_list.MyListResponse;
import com.example.my_books_backend.service.MyListService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/my-lists")
@RequiredArgsConstructor
public class MyListController {
    private final MyListService myListService;

    @GetMapping("")
    public ResponseEntity<List<MyListResponse>> getAllMyLists() {
        List<MyListResponse> myLists = myListService.getAllMyLists();
        return ResponseEntity.ok(myLists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MyListResponse> getMyListById(@PathVariable Long id) {
        MyListResponse myList = myListService.getMyListById(id);
        return ResponseEntity.ok(myList);
    }

    @PostMapping("")
    public ResponseEntity<MyListResponse> createMyList(
            @Valid @RequestBody CreateMyListRequest request) {
        MyListResponse myList = myListService.createMyList(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(myList.getId()).toUri();
        return ResponseEntity.created(location).body(myList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMyList(@PathVariable Long id) {
        myListService.deleteMyList(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MyListResponse>> getMyListsByUserId(@PathVariable Long userId) {
        List<MyListResponse> myLists = myListService.getMyListsByUserId(userId);
        return ResponseEntity.ok(myLists);
    }

    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<MyListResponse>> getMyListsByBookId(@PathVariable String bookId) {
        List<MyListResponse> myLists = myListService.getMyListsByBookId(bookId);
        return ResponseEntity.ok(myLists);
    }
}
