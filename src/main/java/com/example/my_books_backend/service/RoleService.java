package com.example.my_books_backend.service;

import java.util.List;
import com.example.my_books_backend.dto.role.RoleRequest;
import com.example.my_books_backend.dto.role.RoleResponse;

public interface RoleService {
    List<RoleResponse> getAllRoles();

    RoleResponse getRoleById(Long id);

    RoleResponse createRole(RoleRequest request);

    RoleResponse updateRole(Long id, RoleRequest request);

    void deleteRole(Long id);
}
