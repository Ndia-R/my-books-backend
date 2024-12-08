package com.example.my_books_backend.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.my_books_backend.dto.user.UserCreateDto;
import com.example.my_books_backend.dto.user.UserDto;
import com.example.my_books_backend.dto.user.UserUpdateDto;
import com.example.my_books_backend.exception.ConflictException;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.mapper.UserMapper;
import com.example.my_books_backend.model.User;
import com.example.my_books_backend.repository.UserRepository;
import com.example.my_books_backend.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDto signup(String email, String password) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("User already exists: " + email);
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        User saveUser = userRepository.save(user);
        UserDto userDto = userMapper.toDto(saveUser);
        return userDto;
    }

    @Override
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> usersDto = userMapper.toDtoList(users);
        return usersDto;
    }

    @Override
    public UserDto getUserById(Integer id) {
        User user = findUserById(id);
        UserDto userDto = userMapper.toDto(user);
        return userDto;
    }

    @Override
    public UserDto createUser(UserCreateDto dto) {
        User user = userMapper.toEntity(dto);
        User saveUser = userRepository.save(user);
        UserDto userDto = userMapper.toDto(saveUser);
        return userDto;
    }

    @Override
    public void updateUser(Integer id, UserUpdateDto dto) {
        User user = findUserById(id);
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        user.setAvatarUrl(dto.getAvatarUrl());
        userRepository.save(user);
    }

    @Override
    public void patchUser(Integer id, Map<String, Object> updates) {
        User user = findUserById(id);
        updates.forEach((key, value) -> {
            switch (key) {
                case "name":
                    user.setName((String) value);
                    break;
                case "email":
                    user.setEmail((String) value);
                    break;
                case "password":
                    user.setPassword(passwordEncoder.encode((String) value));
                    break;
                case "avatarUrl":
                    user.setAvatarUrl((String) value);
                    break;
            }
        });
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Integer id) {
        User user = findUserById(id);
        userRepository.delete(user);
    }

    private User findUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Not found with this ID: " + id));
        return user;
    }
}
