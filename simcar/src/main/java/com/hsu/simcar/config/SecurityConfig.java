package com.hsu.simcar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 웹 프론트엔드 도메인 허용
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "https://simcar.netlify.app"
        ));
        
        // 모바일 앱의 경우 Origin이 null이거나 다른 형태일 수 있음
        configuration.addAllowedOriginPattern("*");
        
        // ...existing code...
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));
        
        // 모바일 앱에서 필요한 추가 헤더 포함
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "Accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers",
            "User-Agent",        // 모바일 앱 식별용
            "Accept-Language",   // 다국어 지원
            "Cache-Control"      // 캐시 제어
        ));
        
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .requiresChannel(channel -> 
                channel.requestMatchers(r -> r.getHeader("X-Forwarded-Proto") != null)
                .requiresSecure()
            )
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .headers(headers -> {
                headers.frameOptions(Customizer.withDefaults());
                headers.xssProtection(Customizer.withDefaults());
                headers.contentSecurityPolicy(csp -> 
                    csp.policyDirectives("frame-ancestors 'self'")
                );
            })
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/swagger-ui/**", 
                    "/v3/api-docs/**", 
                    "/swagger-resources/**",
                    "/swagger-ui.html",
                    "/h2-console/**",
                    "/uploads/**",
                    "/api/**",
                    "/error").permitAll()
                .anyRequest().authenticated()
            )
            .build();
    }
}