package com.example.my_books_backend.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.my_books_backend.dto.user.UserCreateDto;
import com.example.my_books_backend.dto.user.UserDto;
import com.example.my_books_backend.dto.user.UserUpdateDto;
import com.example.my_books_backend.exception.ConflictException;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.mapper.UserMapper;
import com.example.my_books_backend.model.User;
import com.example.my_books_backend.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional
    public UserDto signup(String email, String password) {
        if (userRepository.findByEmail(email) != null) {
            throw new ConflictException("User already exists: " + email);
        }
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        User saveUser = userRepository.save(user);
        UserDto userDto = userMapper.toDto(saveUser);
        return userDto;
    }

    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> usersDto = userMapper.toDtoList(users);
        return usersDto;
    }

    public UserDto getUserById(Integer id) {
        User user = findUserById(id);
        UserDto userDto = userMapper.toDto(user);
        return userDto;
    }

    @Transactional
    public UserDto createUser(UserCreateDto dto) {
        User user = userMapper.toEntity(dto);
        User saveUser = userRepository.save(user);
        UserDto userDto = userMapper.toDto(saveUser);
        return userDto;
    }

    @Transactional
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

    @Transactional
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

    @Transactional
    public void deleteUser(Integer id) {
        User user = findUserById(id);
        userRepository.delete(user);
    }

    private User findUserById(Integer id) {
        Optional<User> found = userRepository.findById(id);
        if (found.isEmpty()) {
            throw new NotFoundException("Not found with this ID: " + id);
        }
        return found.get();
    }
}
