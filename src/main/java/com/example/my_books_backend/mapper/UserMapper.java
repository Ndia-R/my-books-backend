package com.example.my_books_backend.mapper;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.dto.user.UserProfileResponse;
import com.example.my_books_backend.dto.user.UserResponse;
import com.example.my_books_backend.entity.User;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final ModelMapper modelMapper;

    public UserResponse toUserResponse(User user) {
        UserResponse response = modelMapper.map(user, UserResponse.class);
        List<String> roles =
                user.getRoles().stream().map(role -> role.getName().toString()).toList();
        response.setRoles(roles);
        return response;
    }

    public List<UserResponse> toUserResponseList(List<User> users) {
        return users.stream().map(user -> toUserResponse(user)).toList();
    }

    public UserProfileResponse toUserProfileResponse(User user) {
        UserProfileResponse response = modelMapper.map(user, UserProfileResponse.class);
        List<String> roles =
                user.getRoles().stream().map(role -> role.getName().toString()).toList();
        response.setRoles(roles);
        return response;
    }

    public List<UserProfileResponse> toUserProfileResponseList(List<User> users) {
        return users.stream().map(user -> toUserProfileResponse(user)).toList();
    }
}

