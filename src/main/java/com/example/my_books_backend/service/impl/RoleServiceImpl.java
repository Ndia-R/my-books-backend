package com.example.my_books_backend.service.impl;

import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import com.example.my_books_backend.dto.role.CreateRoleRequest;
import com.example.my_books_backend.dto.role.RoleResponse;
import com.example.my_books_backend.dto.role.UpdateRoleRequest;
import com.example.my_books_backend.entity.Role;
import com.example.my_books_backend.entity.RoleName;
import com.example.my_books_backend.exception.NotFoundException;
import com.example.my_books_backend.mapper.RoleMapper;
import com.example.my_books_backend.repository.RoleRepository;
import com.example.my_books_backend.service.RoleService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    @Cacheable("getAllRoles")
    public List<com.example.my_books_backend.dto.role.RoleResponse> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return roleMapper.toRoleResponseList(roles);
    }

    @Override
    public RoleResponse getRoleById(Long id) {
        Role role = findRoleById(id);
        return roleMapper.toRoleResponse(role);
    }

    @Override
    public RoleResponse createRole(CreateRoleRequest request) {
        Role role = roleMapper.toRoleEntity(request);
        Role saveRole = roleRepository.save(role);
        return roleMapper.toRoleResponse(saveRole);
    }

    @Override
    public void updateRole(Long id, UpdateRoleRequest request) {
        Role role = findRoleById(id);

        RoleName name = request.getName();
        String description = request.getDescription();

        if (name != null) {
            role.setName(name);
        }

        if (description != null) {
            role.setDescription(description);
        }
        roleRepository.save(role);
    }

    @Override
    public void deleteRole(Long id) {
        Role role = findRoleById(id);
        roleRepository.delete(role);
    }

    private Role findRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("見つかりませんでした。 ID: " + id));
        return role;
    }
}
