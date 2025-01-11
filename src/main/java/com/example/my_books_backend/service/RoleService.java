package com.example.my_books_backend.service;

import java.util.List;
import com.example.my_books_backend.dto.role.CreateRoleRequest;
import com.example.my_books_backend.dto.role.RoleResponse;
import com.example.my_books_backend.dto.role.UpdateRoleRequest;

public interface RoleService {
    List<RoleResponse> getAllRoles();

    RoleResponse getRoleById(Long id);

    RoleResponse createRole(CreateRoleRequest request);

    void updateRole(Long id, UpdateRoleRequest request);

    void deleteRole(Long id);
}
