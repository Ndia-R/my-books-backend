package com.example.my_books_backend.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.example.my_books_backend.dto.user.UserCreateDto;
import com.example.my_books_backend.dto.user.UserDto;
import com.example.my_books_backend.dto.user.UserUpdateDto;
import com.example.my_books_backend.model.User;

public interface UserService {

    Optional<User> findByEmail(String email);

    UserDto signup(String email, String password);

    List<UserDto> getUsers();

    UserDto getUserById(Integer id);

    UserDto createUser(UserCreateDto dto);

    void updateUser(Integer id, UserUpdateDto dto);

    void patchUser(Integer id, Map<String, Object> updates);

    void deleteUser(Integer id);
}
