package com.example.my_books_backend.config;

import java.util.List;
import java.util.Arrays;
import org.springframework.stereotype.Component;

@Component
public class SecurityEndpointsConfig {

    public List<String> getFullyPublicEndpoints() {
        return Arrays.asList("/api/v1/login", "/api/v1/signup", "/api/v1/logout",
                "/api/v1/refresh-token", "/api/v1/users/exists", "/v3/api-docs/**",
                "/swagger-ui/**", "/swagger-ui.html");
    }

    public List<String> getPublicGetEndpoints() {
        return Arrays.asList("/api/v1/genres/**", "/api/v1/books/**");
    }
}
