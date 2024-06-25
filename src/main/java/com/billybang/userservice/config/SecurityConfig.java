package com.billybang.userservice.config;

import com.billybang.userservice.exception.AccessDeniedHandlerImpl;
import com.billybang.userservice.exception.AuthenticationExceptionHandler;
import com.billybang.userservice.filter.TokenFilter;
import com.billybang.userservice.service.TokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.Objects;

import static com.billybang.userservice.security.AuthConstant.USER_ID;
import static com.billybang.userservice.security.jwt.JWTConstant.*;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final AntPathRequestMatcher[] AUTH_WHITELIST = {
            antMatcher("/swagger-resources/**"),
            antMatcher("/swagger-ui/**"),
            antMatcher("/**/api-docs/**"),
            antMatcher("/index.html"),
            antMatcher("/error"),
            antMatcher("/favicon.ico"),
            antMatcher("/actuator/**")
    };

    private final TokenFilter tokenFilter;
    private final TokenService tokenService;
    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuthService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                antMatcher("/**/token"),
                                antMatcher("/**/login"),
                                antMatcher("/**/sign-up"),
                                antMatcher("/**/oauth/**"),
                                antMatcher(HttpMethod.GET, "/users/validate-email"),
                                antMatcher(HttpMethod.GET, "/users/validate-token"))
                        .permitAll()
                        .requestMatchers(AUTH_WHITELIST)
                        .permitAll()
                        .requestMatchers(antMatcher(HttpMethod.GET, "/exception/**"))
                        .permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new AuthenticationExceptionHandler())
                        .accessDeniedHandler(new AccessDeniedHandlerImpl())
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuthService))
                        .successHandler(onOAuth2LoginSuccess())
                        .failureHandler(onOAuth2LoginFailure())
                )
                .logout(logout -> logout
                        .logoutUrl("/users/logout")
                        .logoutSuccessHandler(onLogoutSuccess())
                        .deleteCookies(ACCESS_TOKEN_NAME, REFRESH_TOKEN_NAME)
                )
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://www.billybang.me"));
        configuration.setAllowedMethods(List.of("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("**", configuration);
        return source;
    }

    @Bean
    public AuthenticationSuccessHandler onOAuth2LoginSuccess() {
        return (request, response, authentication) -> {
            OAuth2User principal = (OAuth2User) authentication.getPrincipal();

            String accessToken = tokenService.genAccessTokenByEmail(principal.getAttribute("email"));
            String refreshToken = tokenService.genRefreshTokenByEmail(principal.getAttribute("email"));

            addCookies(response, accessToken, refreshToken);

            response.sendRedirect("http://www.billybang.me/"); // TODO: 하드 코딩 제거
        };
    }

    @Bean
    public AuthenticationFailureHandler onOAuth2LoginFailure() {
        return (request, response, exception) -> {
            log.error(exception.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        };
    }

    @Bean
    public LogoutSuccessHandler onLogoutSuccess() {
        return (request, response, authentication) -> response.setStatus(HttpServletResponse.SC_NO_CONTENT);

    }

    private Cookie createCookie(String cookieName, String cookieValue, long maxAge) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) maxAge);
        return cookie;
    }

    private void addCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        response.addCookie(createCookie(ACCESS_TOKEN_NAME, accessToken, ACCESS_TOKEN_MAX_AGE / 1000));
        response.addCookie(createCookie(REFRESH_TOKEN_NAME, refreshToken, REFRESH_TOKEN_MAX_AGE / 1000));
    }
}
