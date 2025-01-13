package com.hsu.simcar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.disable())
            .headers(headers -> headers
                .addHeaderWriter((request, response) -> response.setHeader("X-Frame-Options", "SAMEORIGIN"))) // H2 콘솔용 프레임 허용
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/swagger-ui/**", 
                            "/v3/api-docs/**", 
                            "/swagger-resources/**",
                            "/swagger-ui.html",
                            "/h2-console/**",
                            "/api/login",
                            "/api/logout",
                            "/api/join").permitAll()
                .anyRequest().authenticated()
            )
            .build();
    }
}