package com.project.law.common.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.law.common.filter.CustomJwtFilter;
import com.project.law.common.filter.CustomLoginFilter;
import com.project.law.common.filter.CustomLogoutFilter;
import com.project.law.common.util.CookieUtil;
import com.project.law.common.util.JwtUtil;
import com.project.law.common.util.UrlFilter;
import com.project.law.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                          AuthenticationManager authenticationManager) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .headers((e) -> e.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .cors(cors -> cors.configurationSource(corsConfiguration()))

                .addFilterBefore(new CustomJwtFilter(jwtUtil, cookieUtil), UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(new CustomLoginFilter(authenticationManager, jwtUtil, cookieUtil ,bCryptPasswordEncoder, stringRedisTemplate, userRepository, objectMapper), UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(new CustomLogoutFilter(stringRedisTemplate, jwtUtil, cookieUtil), LogoutFilter.class)

                .authorizeHttpRequests(request -> {
                    if (true) {
                        request
                                .requestMatchers(UrlFilter.getSecurityFilterPassPath()).permitAll()
//                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/api-docs/**").permitAll()
                                .anyRequest().authenticated();
                    } else {
                        request.anyRequest().permitAll();
                    }
                });
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfiguration() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:8080",
                "http://localhost:8080"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE","OPTIONS"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(Collections.singletonList("*")); // 개발용
        config.setAllowedHeaders(Collections.singletonList("*")); // 개발용
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
