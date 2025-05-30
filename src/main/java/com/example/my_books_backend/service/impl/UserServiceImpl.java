package com.example.my_books_backend.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.my_books_backend.dto.user.UpdateUserEmailRequest;
import com.example.my_books_backend.dto.user.UpdateUserPasswordRequest;
import com.example.my_books_backend.dto.user.CreateUserRequest;
import com.example.my_books_backend.dto.user.UserProfileCountsResponse;
import com.example.my_books_backend.dto.user.UserProfileResponse;
import com.example.my_books_backend.dto.user.UserResponse;
import com.example.my_books_backend.dto.user.UpdateUserProfileRequest;
import com.example.my_books_backend.entity.Role;
import com.example.my_books_backend.entity.RoleName;
import com.example.my_books_backend.entity.User;
import com.example.my_books_backend.exception.ConflictException;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.exception.UnauthorizedException;
import com.example.my_books_backend.exception.ValidationException;
import com.example.my_books_backend.mapper.UserMapper;
import com.example.my_books_backend.repository.BookmarkRepository;
import com.example.my_books_backend.repository.FavoriteRepository;
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
    private final BookmarkRepository bookmarkRepository;
    private final ReviewRepository reviewRepository;

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RandomStringUtil randomStringUtil;

    private String DEFAULT_AVATAR_PATH = "";

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toUserResponseList(users);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        return userMapper.toUserResponse(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public User createUser(CreateUserRequest request) {
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setAvatarPath(request.getAvatarPath());

        if (user.getRoles() == null) {
            Role role = roleRepository.findByName(RoleName.ROLE_USER);
            user.setRoles(Collections.singletonList(role));
        }

        if (user.getName() == null) {
            String name = "USER_" + randomStringUtil.generateRandomString();
            user.setName(name);
        }

        if (user.getAvatarPath() == null) {
            String avatarPath = DEFAULT_AVATAR_PATH;
            user.setAvatarPath(avatarPath);
        }

        User savedUser = userRepository.save(user);
        return savedUser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserProfileResponse getUserProfile(User user) {
        return userMapper.toUserProfileResponse(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserProfileCountsResponse getUserProfileCounts(User user) {
        Integer favoriteCount = favoriteRepository.countByUserIdAndIsDeletedFalse(user.getId());
        Integer bookmarkCount = bookmarkRepository.countByUserIdAndIsDeletedFalse(user.getId());
        Integer reviewCount = reviewRepository.countByUserIdAndIsDeletedFalse(user.getId());

        return new UserProfileCountsResponse(favoriteCount, bookmarkCount, reviewCount);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void updateUserProfile(UpdateUserProfileRequest request, User user) {
        String name = request.getName();
        String avatarPath = request.getAvatarPath();

        if (name != null) {
            user.setName(name);
        }
        if (avatarPath != null) {
            user.setAvatarPath(avatarPath);
        }
        userRepository.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void updateUserEmail(UpdateUserEmailRequest request, User user) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void updateUserPassword(UpdateUserPasswordRequest request, User user) {
        String currentPassword = request.getCurrentPassword();
        String newPassword = request.getNewPassword();
        String confirmPassword = request.getConfirmPassword();

        if (!newPassword.equals(confirmPassword)) {
            throw new ValidationException("新しいパスワードと確認用パスワードが一致していません。");
        }

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new UnauthorizedException("現在のパスワードが間違っています。");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
