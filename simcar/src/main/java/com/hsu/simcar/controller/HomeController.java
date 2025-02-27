package com.hsu.simcar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    // API 경로 확인용 엔드포인트
    @GetMapping("/api/health") 
    public String healthCheck() {
        return "OK";
    }
    
    // SPA의 다른 경로들을 처리
    @GetMapping(value = {"/", "/{path:^(?!api|swagger-ui|v3/api-docs|h2-console).*$}/**"})
    public String forward() {
        return "forward:/index.html";
    }
}