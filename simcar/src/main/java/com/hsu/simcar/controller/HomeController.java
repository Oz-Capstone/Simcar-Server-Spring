package com.hsu.simcar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping(value = "/")
    public String index() {
        return "forward:/index.html";
    }

    @GetMapping(value = "/{path:^(?!api|swagger-ui|v3/api-docs|h2-console).*}/**")
    public String forwardToIndex() {
        return "forward:/index.html";
    }
}