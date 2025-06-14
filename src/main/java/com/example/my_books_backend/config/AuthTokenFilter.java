package com.example.my_books_backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.my_books_backend.service.impl.UserDetailsServiceImpl;
import com.example.my_books_backend.util.JwtUtils;

@Component
@RequiredArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {
    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtil;
    private final SecurityEndpointsConfig securityEndpointsConfig;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();

        List<String> fullyPublicEndpoints = securityEndpointsConfig.getFullyPublicEndpoints();
        List<String> publicGetEndpoints = securityEndpointsConfig.getPublicGetEndpoints();

        // `permitAll()` のエンドポイントならフィルターをスキップ
        if (fullyPublicEndpoints.stream()
                .anyMatch(endpoint -> pathMatcher.match(endpoint, requestURI))
                || ("GET".equals(method) && publicGetEndpoints.stream()
                        .anyMatch(endpoint -> pathMatcher.match(endpoint, requestURI)))) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = getTokenFromHeader(request);
            if (token != null && jwtUtil.validateToken(token)) {
                String email = jwtUtil.getSubjectFromToken(token);

                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null,
                                userDetails.getAuthorities());

                authentication
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if (token != null) {
                logger.warn("無効なトークン: {}", token);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
                return;
            }
        } catch (Exception e) {
            logger.error("ユーザー認証を設定できません: {}", e);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"An unexpected error occurred\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
