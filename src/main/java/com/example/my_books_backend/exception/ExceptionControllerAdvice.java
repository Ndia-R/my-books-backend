package com.example.my_books_backend.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionControllerAdvice extends ResponseEntityExceptionHandler {
        @ExceptionHandler({NotFoundException.class})
        public ResponseEntity<Object> handleNotFound(NotFoundException ex, WebRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(Arrays.asList(ex.getMessage()),
                                HttpStatus.NOT_FOUND);
                return this.handleExceptionInternal(ex, errorResponse, new HttpHeaders(),
                                HttpStatus.NOT_FOUND, request);
        }

        @ExceptionHandler({BadRequestException.class})
        public ResponseEntity<Object> handleBadRequest(BadRequestException ex, WebRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(Arrays.asList(ex.getMessage()),
                                HttpStatus.BAD_REQUEST);
                return this.handleExceptionInternal(ex, errorResponse, new HttpHeaders(),
                                HttpStatus.BAD_REQUEST, request);
        }

        @ExceptionHandler({ConflictException.class})
        public ResponseEntity<Object> handleConflict(ConflictException ex, WebRequest request) {
                ErrorResponse errorResponse = new ErrorResponse(Arrays.asList(ex.getMessage()),
                                HttpStatus.CONFLICT);
                return this.handleExceptionInternal(ex, errorResponse, new HttpHeaders(),
                                HttpStatus.CONFLICT, request);
        }

        @Override
        @SuppressWarnings("null")
        protected ResponseEntity<Object> handleMethodArgumentNotValid(
                        MethodArgumentNotValidException ex, HttpHeaders headers,
                        HttpStatusCode status, WebRequest request) {
                ErrorResponse errorResponse = buildErrorResponse(ex.getBindingResult());
                return this.handleExceptionInternal(ex, errorResponse, headers,
                                HttpStatus.BAD_REQUEST, request);
        }

        private ErrorResponse buildErrorResponse(BindingResult bindingResult) {
                List<FieldError> fieldErrors = bindingResult.getFieldErrors();
                List<String> errorMessages = new ArrayList<>();
                for (final FieldError error : fieldErrors) {
                        errorMessages.add(error.getField() + ": " + error.getDefaultMessage());
                }
                ErrorResponse errorResponse =
                                new ErrorResponse(errorMessages, HttpStatus.BAD_REQUEST);
                return errorResponse;
        }
}
