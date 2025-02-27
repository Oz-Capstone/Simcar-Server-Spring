package com.hsu.simcar.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 모든 경로에 대해 index.html 리턴 (SPA 라우팅을 위함)
        registry.addViewController("/").setViewName("forward:/index.html");
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/static/")
            .resourceChain(true)
            .addResolver(new PathResourceResolver() {
                @Override
                protected Resource getResource(String resourcePath, Resource location) throws IOException {
                    Resource requestedResource = location.createRelative(resourcePath);
                    
                    // 요청한 리소스가 존재하면 반환, 아니면 index.html 반환
                    return requestedResource.exists() && requestedResource.isReadable() ? 
                            requestedResource : new ClassPathResource("/static/index.html");
                }
            });
            
        // 업로드 폴더 설정
        registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:uploads/");
    }
}