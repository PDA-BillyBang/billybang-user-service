package com.billybang.userservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "BillyBang User Service API",
                description = "BillyBang 에서 개발 중인 유저 서비스 API 문서",
                version = "v1"))
@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi userServiceOpenApi() {
        String[] paths = {"/users/**"};

        return GroupedOpenApi.builder()
                .group("User Service API")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public OpenAPI securityOpenApi() {
        final String DEBUG_MODE = "debug";
        final String COOKIE = "cookie";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement()
                        .addList(DEBUG_MODE)
                        .addList(COOKIE))
                .components(new Components()
                        .addSecuritySchemes(DEBUG_MODE, new SecurityScheme()
                                .name("debug mode")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .description("API-KEY"))
                        .addSecuritySchemes(COOKIE, new SecurityScheme()
                                .name("JWT")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE))
                );
    }
}
