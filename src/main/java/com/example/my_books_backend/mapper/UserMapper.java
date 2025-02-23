package com.example.my_books_backend.mapper;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.dto.user.UserResponse;
import com.example.my_books_backend.entity.User;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final ModelMapper modelMapper;

    public UserResponse toUserResponse(User user) {
        UserResponse userResponse = modelMapper.map(user, UserResponse.class);
        List<String> roles =
                user.getRoles().stream().map(role -> role.getName().toString()).toList();
        userResponse.setRoles(roles);
        return userResponse;
    }

    public List<UserResponse> toUserResponseList(List<User> users) {
        return users.stream().map(user -> toUserResponse(user)).toList();
    }
}
