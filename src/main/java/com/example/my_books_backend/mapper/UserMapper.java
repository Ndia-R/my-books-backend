package com.example.my_books_backend.mapper;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.dto.user.UserCreateDto;
import com.example.my_books_backend.dto.user.UserDto;
import com.example.my_books_backend.model.User;

@Component
public class UserMapper {

    @Autowired
    private ModelMapper modelMapper;

    public UserDto toDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }

    public User toEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    public User toEntity(UserCreateDto createUserDto) {
        return modelMapper.map(createUserDto, User.class);
    }

    public List<UserDto> toDtoList(List<User> users) {
        return users.stream().map(user -> toDto(user)).toList();
    }
}
