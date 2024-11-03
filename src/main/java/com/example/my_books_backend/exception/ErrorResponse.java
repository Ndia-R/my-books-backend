package com.example.my_books_backend.exception;

import java.util.List;
import org.springframework.http.HttpStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private List<String> messages;
    private HttpStatus status;
}
