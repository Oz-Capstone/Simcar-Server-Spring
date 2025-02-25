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
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

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