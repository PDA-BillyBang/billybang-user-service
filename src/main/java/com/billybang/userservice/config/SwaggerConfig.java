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
                description = "BillyBang에서 개발 중인 유저 서비스 API 문서",
                version = "v1"))
@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi demoOpenApi() { // application.yml 을 통해서도 설정 가능
        String[] paths = {"/api/demo/**"};

        return GroupedOpenApi.builder()
                .group("Demo API v1")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public GroupedOpenApi userServiceOpenApi() {
        String[] paths = {"/api/users/**"};

        return GroupedOpenApi.builder()
                .group("User Service API")
                .pathsToMatch(paths)
                .build();
    }

    @Bean
    public OpenAPI securityOpenApi() {
        final String DEBUG_MODE = "debug";
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement()
                        .addList(DEBUG_MODE))
                .components(new Components()
                        .addSecuritySchemes(DEBUG_MODE, new SecurityScheme()
                                .name("Authorization")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .description("API-KEY")));
    }
}
