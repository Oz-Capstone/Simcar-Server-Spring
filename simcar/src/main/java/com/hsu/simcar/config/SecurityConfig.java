package com.hsu.simcar.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
            "https://localhost:3000",
            "http://simcar.netlify.app",
            "https://simcar.netlify.app",
            "http://simcar.kro.kr",
            "https://simcar.kro.kr"
        ));
        
        // 모바일 앱의 경우 Origin이 null이거나 다른 형태일 수 있음
        configuration.addAllowedOriginPattern("*");
        
        // 허용할 HTTP 메서드 설정
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
        
        // 브라우저가 응답에 액세스할 수 있는 헤더 설정
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials",
            "Authorization"
        ));
        
        // 자격 증명 허용 (쿠키, 인증 헤더 등)
        configuration.setAllowCredentials(true);
        
        // preflight 요청에 대한 캐시 시간 설정 (1시간)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // HTTPS 리다이렉션 설정
            .requiresChannel(channel -> 
                channel.anyRequest().requiresSecure()
            )
            // CSRF 보호 비활성화 (REST API에서는 필요 없음)
            .csrf(csrf -> csrf.disable())
            
            // CORS 설정 적용
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // 보안 헤더 설정 - 최신 API 사용
            .headers(headers -> {
                headers.frameOptions(frameOptions -> frameOptions.sameOrigin());  // H2 콘솔 접근을 위해 필요
                headers.xssProtection(xss -> xss.disable());  // XSS 보호 비활성화
                headers.contentSecurityPolicy(csp -> 
                    csp.policyDirectives("default-src 'self'; connect-src *; img-src * data:; style-src 'self' 'unsafe-inline'; script-src 'self' 'unsafe-inline' 'unsafe-eval';")
                );
            })
            
            // URL별 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/**",
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
            );
        
        return http.build();
    }
}