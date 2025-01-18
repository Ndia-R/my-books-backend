package com.example.my_books_backend.dto.my_list;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyListStateResponse {
    private Boolean isMyList;
    private Integer myListCount;
}
