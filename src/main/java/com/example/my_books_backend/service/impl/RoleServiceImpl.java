package com.example.my_books_backend.service.impl;

import java.util.List;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.my_books_backend.dto.role.RoleRequest;
import com.example.my_books_backend.dto.role.RoleResponse;
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
    public List<RoleResponse> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return roleMapper.toRoleResponseList(roles);
    }

    @Override
    @Cacheable(value = "getRoleById", key = "#p0")
    public RoleResponse getRoleById(Long id) {
        Role role = findRoleById(id);
        return roleMapper.toRoleResponse(role);
    }

    @Override
    @Transactional
    @CacheEvict(value = "getAllRoles", allEntries = true)
    public RoleResponse createRole(RoleRequest request) {
        Role role = new Role();
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        Role saveRole = roleRepository.save(role);
        return roleMapper.toRoleResponse(saveRole);
    }

    @Override
    @Transactional
    @Caching(evict = {@CacheEvict(value = "getRoleById", key = "#p0"),
            @CacheEvict(value = "getAllRoles", allEntries = true)})
    public RoleResponse updateRole(Long id, RoleRequest request) {
        Role role = findRoleById(id);

        RoleName name = request.getName();
        String description = request.getDescription();

        if (name != null) {
            role.setName(name);
        }

        if (description != null) {
            role.setDescription(description);
        }
        Role savedRole = roleRepository.save(role);
        return roleMapper.toRoleResponse(savedRole);
    }

    @Override
    @Transactional
    @Caching(evict = {@CacheEvict(value = "getRoleById", key = "#p0"),
            @CacheEvict(value = "getAllRoles", allEntries = true)})
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
