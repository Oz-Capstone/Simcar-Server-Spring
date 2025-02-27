package com.hsu.simcar.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ForwardController {
    @RequestMapping(value = "/**")
    public String forward() {
        return "forward:/index.html";
    }
}