package com.example.my_books_backend.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.my_books_backend.dto.user.ChangeEmailRequest;
import com.example.my_books_backend.dto.user.ChangePasswordRequest;
import com.example.my_books_backend.dto.user.CreateUserRequest;
import com.example.my_books_backend.dto.user.ProfileCountsResponse;
import com.example.my_books_backend.dto.user.UserResponse;
import com.example.my_books_backend.dto.user.UpdateUserRequest;
import com.example.my_books_backend.entity.Role;
import com.example.my_books_backend.entity.RoleName;
import com.example.my_books_backend.entity.User;
import com.example.my_books_backend.exception.ConflictException;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.exception.UnauthorizedException;
import com.example.my_books_backend.exception.ValidationException;
import com.example.my_books_backend.mapper.UserMapper;
import com.example.my_books_backend.repository.FavoriteRepository;
import com.example.my_books_backend.repository.MyListRepository;
import com.example.my_books_backend.repository.ReviewRepository;
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
    private final FavoriteRepository favoriteRepository;
    private final MyListRepository myListRepository;
    private final ReviewRepository reviewRepository;

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RandomStringUtil randomStringUtil;

    @Value("${spring.app.defaultAvatarUrl}")
    private String DEFAULT_AVATAR_URL;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toUserResponseList(users);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = findUserById(id);
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse createUser(CreateUserRequest request) {
        User user = userMapper.toUserEntity(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));

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
        return userMapper.toUserResponse(saveUser);
    }

    @Override
    public UserResponse getCurrentUser() {
        User user = getAuthenticatedUser();
        return userMapper.toUserResponse(user);
    }

    @Override
    public ProfileCountsResponse getProfileCounts() {
        User user = getAuthenticatedUser();
        Integer favoritesCount = favoriteRepository.countByUserIdAndIsDeletedFalse(user.getId());
        Integer myListCount = myListRepository.countByUserIdAndIsDeletedFalse(user.getId());
        Integer reviewCount = reviewRepository.countByUserIdAndIsDeletedFalse(user.getId());

        return new ProfileCountsResponse(favoritesCount, myListCount, reviewCount);
    }

    @Override
    public void updateCurrentUser(UpdateUserRequest request) {
        User user = getAuthenticatedUser();

        String name = request.getName();
        String avatarUrl = request.getAvatarUrl();

        if (name != null) {
            user.setName(name);
        }
        if (avatarUrl != null) {
            user.setAvatarUrl(avatarUrl);
        }
        userRepository.save(user);
    }

    @Override
    public void changeEmail(ChangeEmailRequest request) {
        User user = getAuthenticatedUser();

        String email = request.getEmail();
        String password = request.getPassword();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new UnauthorizedException("パスワードが間違っています。");
        }

        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("このメールアドレスは既に登録されています。: " + email);
        }

        // 本来はここで新しいメールアドレスにメールを送ってメール内のリンクを
        // クリックしてもらうなどで、新しいメールアドレスが本人のものであるか
        // 確認してから、メールアドレスを更新する

        user.setEmail(email);
        userRepository.save(user);
    }

    @Override
    public void changePassword(ChangePasswordRequest request) {
        User user = getAuthenticatedUser();

        String newPassword = request.getNewPassword();
        String confirmNewPassword = request.getConfirmNewPassword();
        String currentPassword = request.getCurrentPassword();

        if (!newPassword.equals(confirmNewPassword)) {
            throw new ValidationException("新しいパスワードと確認用パスワードが一致していません。");
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new UnauthorizedException("現在のパスワードが間違っています。");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        User user = findUserById(id);
        userRepository.delete(user);
    }

    private User findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("見つかりませんでした。 ID: " + id));
        return user;
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User authenticatedUser = (User) authentication.getPrincipal();
        return authenticatedUser;
    }

    @Override
    public Boolean checkNameExists(String name) {
        return userRepository.existsByName(name);
    }
}
