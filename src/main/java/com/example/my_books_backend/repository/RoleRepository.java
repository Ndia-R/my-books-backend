package com.example.my_books_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.my_books_backend.model.Role;
import com.example.my_books_backend.model.RoleName;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByName(RoleName name);
}
