package com.hsu.simcar.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
            .addResourceHandler("/uploads/**")
            .addResourceLocations("file:uploads/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 루트 경로 매핑 추가
        registry.addViewController("/")
            .setViewName("forward:/index.html");
            
        // 기존 매핑
        registry.addViewController("/{spring:\\w+}")
            .setViewName("forward:/index.html");
        registry.addViewController("/**/{spring:\\w+}")
            .setViewName("forward:/index.html");
        registry.addViewController("/{spring:\\w+}/**{spring:?!(\\.js|\\.css|\\.png|\\.jpg|\\.jpeg)$}")
            .setViewName("forward:/index.html");
    }
}