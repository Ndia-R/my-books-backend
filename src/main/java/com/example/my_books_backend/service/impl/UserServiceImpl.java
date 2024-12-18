package com.example.my_books_backend.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.my_books_backend.dto.user.EmailChangeDto;
import com.example.my_books_backend.dto.user.PasswordChangeDto;
import com.example.my_books_backend.dto.user.UserCreateDto;
import com.example.my_books_backend.dto.user.UserDto;
import com.example.my_books_backend.dto.user.UserUpdateDto;
import com.example.my_books_backend.exception.ConflictException;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.exception.UnauthorizedException;
import com.example.my_books_backend.exception.ValidationException;
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

    @Value("${spring.app.default.avatarUrl}")
    private final String DEFAULT_AVATAR_URL;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDto> getUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toDtoList(users);
    }

    @Override
    public UserDto getUserById(Integer id) {
        User user = findUserById(id);
        return userMapper.toDto(user);
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
        return userMapper.toDto(saveUser);
    }

    @Override
    public UserDto getCurrentUser() {
        User user = getAuthenticatedUser();
        return userMapper.toDto(user);
    }

    @Override
    public void updateCurrentUser(UserUpdateDto dto) {
        User user = getAuthenticatedUser();

        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        if (dto.getAvatarUrl() != null) {
            user.setAvatarUrl(dto.getAvatarUrl());
        }
        userRepository.save(user);
    }

    @Override
    public void changeEmail(EmailChangeDto dto) {
        User user = getAuthenticatedUser();

        if (userRepository.existsByEmail(dto.getNewEmail())) {
            throw new ConflictException("このメールアドレスは既に登録されています。: " + dto.getNewEmail());
        }

        // 本来はここで新しいメールアドレスにメールを送ってメール内のリンクを
        // クリックしてもらうなどで、新しいメールアドレスが本人のものであるか
        // 確認してから、メールアドレスを更新する

        user.setEmail(dto.getNewEmail());
        userRepository.save(user);
    }

    @Override
    public void changePassword(PasswordChangeDto dto) {
        User user = getAuthenticatedUser();

        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new ValidationException("新しいパスワードと確認用パスワードが一致していません。");
        }

        if (!passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedException("現在のパスワードが間違っています。");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Integer id) {
        User user = findUserById(id);
        userRepository.delete(user);
    }

    private User findUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("見つかりませんでした。 ID: " + id));
        return user;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal();
        return authenticatedUser;
    }

}
