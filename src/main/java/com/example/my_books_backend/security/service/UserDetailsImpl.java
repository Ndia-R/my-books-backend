package com.example.my_books_backend.security.service;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.example.my_books_backend.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

@NoArgsConstructor
@Data
public class UserDetailsImpl implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String username;
    private String email;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Integer id, String username, String email, String password,
            Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        // GrantedAuthority authority =
        // new SimpleGrantedAuthority(user.getRole().getRoleName().name());
        GrantedAuthority authority = (GrantedAuthority) new ArrayList<>();

        return new UserDetailsImpl(user.getId(), user.getName(), user.getEmail(),
                user.getPassword(), List.of(authority));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public Integer getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    // ユーザーアカウントが有効期限切れでないかを判定する
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // ユーザーアカウントがロックされていないかを判定する
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 認証情報が有効期限切れでないかを判定する
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
