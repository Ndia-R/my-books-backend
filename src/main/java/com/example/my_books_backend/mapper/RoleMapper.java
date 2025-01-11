package com.example.my_books_backend.mapper;

import java.util.List;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import com.example.my_books_backend.dto.role.CreateRoleRequest;
import com.example.my_books_backend.dto.role.RoleResponse;
import com.example.my_books_backend.entity.Role;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RoleMapper {
    private final ModelMapper modelMapper;

    public RoleResponse toRoleResponse(Role role) {
        return modelMapper.map(role, RoleResponse.class);
    }

    public List<RoleResponse> toRoleResponseList(List<Role> roles) {
        return roles.stream().map(role -> toRoleResponse(role)).toList();
    }

    public Role toRoleEntity(CreateRoleRequest createRoleRequest) {
        return modelMapper.map(createRoleRequest, Role.class);
    }
}
