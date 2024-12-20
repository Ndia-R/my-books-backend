package com.example.my_books_backend.mapper;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.dto.user.CreateUserRequest;
import com.example.my_books_backend.dto.user.UserResponse;
import com.example.my_books_backend.entity.User;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final ModelMapper modelMapper;

    public UserResponse toResponse(User user) {
        UserResponse userResponse = modelMapper.map(user, UserResponse.class);

        List<String> roles = user.getRoles().stream().map(role -> role.getName()).toList();
        userResponse.setRoles(roles);

        return userResponse;
    }

    public User toEntity(UserResponse userResponse) {
        return modelMapper.map(userResponse, User.class);
    }

    public User toEntity(CreateUserRequest createUserRequest) {
        return modelMapper.map(createUserRequest, User.class);
    }

    public List<UserResponse> toResponseList(List<User> users) {
        return users.stream().map(user -> toResponse(user)).toList();
    }
}
