package com.hsu.simcar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {
    
    @GetMapping("/")
    @ResponseBody
    public String home() {
        return "서버가 정상적으로 동작 중입니다. 정적 파일이 올바르게 로드되지 않고 있습니다.";
    }
}