package com.example.my_books_backend.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.my_books_backend.dto.user.UserCreateDto;
import com.example.my_books_backend.dto.user.UserDto;
import com.example.my_books_backend.dto.user.UserUpdateDto;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.mapper.UserMapper;
import com.example.my_books_backend.model.Role;
import com.example.my_books_backend.model.RoleName;
import com.example.my_books_backend.model.User;
import com.example.my_books_backend.repository.RoleRepository;
import com.example.my_books_backend.repository.UserRepository;
import com.example.my_books_backend.service.UserService;
import com.example.my_books_backend.util.RandomStringUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RandomStringUtil randomStringUtil;

    private final String DEFAULT_AVATAR_URL = "http://localhost:18080/images/avatars/avatar00.jpg";

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
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

        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        if (user.getRoles() == null) {
            Role role = roleRepository.findByName(RoleName.ROLE_USER);
            user.setRoles(Collections.singletonList(role));
        }

        if (user.getName() == null) {
            String name = "USER_" + randomStringUtil.generateRandomString();
            user.setName(name);
        }

        if (user.getAvatarUrl() == null) {
            String avatarUrl = DEFAULT_AVATAR_URL;
            user.setAvatarUrl(avatarUrl);
        }

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
