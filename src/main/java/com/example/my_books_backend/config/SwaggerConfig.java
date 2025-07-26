package com.example.my_books_backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${app.swagger.server.url:http://localhost:8080}")
    private String serverUrl;

    @Value("${app.swagger.server.description:Local server}")
    private String serverDescription;

    @Bean
    public OpenAPI customOpenAPI() {
        // セキュリティスキームの定義
        SecurityScheme securityScheme = new SecurityScheme().type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT");

        // SecurityRequirementの追加
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearerAuth");

        // サーバー設定
        List<Server> servers = new ArrayList<>();
        servers.add(new Server().url(serverUrl).description(serverDescription));

        // 本番環境（Docker）の場合は追加のサーバーオプションを提供
        if (serverUrl.contains("/api/v1")) {
            servers.add(
                new Server()
                    .url("http://localhost/api/v1")
                    .description("Local HTTP server (redirected to HTTPS)")
            );
        }

        return new OpenAPI()
            .servers(servers)
            .info(
                new Info()
                    .title("My Books API")
                    .version("1.0")
                    .description("書籍管理API")
            )
            .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
            .addSecurityItem(securityRequirement);
    }
}