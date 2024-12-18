package com.example.my_books_backend.service;

import java.util.List;
import java.util.Optional;
import com.example.my_books_backend.dto.user.EmailChangeDto;
import com.example.my_books_backend.dto.user.PasswordChangeDto;
import com.example.my_books_backend.dto.user.UserCreateDto;
import com.example.my_books_backend.dto.user.UserDto;
import com.example.my_books_backend.dto.user.UserUpdateDto;
import com.example.my_books_backend.model.User;

public interface UserService {

    Optional<User> findByEmail(String email);

    List<UserDto> getUsers();

    UserDto getUserById(Integer id);

    void deleteUser(Integer id);

    UserDto createUser(UserCreateDto dto);

    UserDto getCurrentUser();

    void updateCurrentUser(UserUpdateDto dto);

    void changeEmail(EmailChangeDto dto);

    void changePassword(PasswordChangeDto dto);


}
