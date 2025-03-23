package com.example.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    /**
     * 处理 /hello 路由的请求，返回 "Hello World" 字符串。
     *
     * @return 包含 "Hello World" 的字符串
     */
    @GetMapping("/hello")
    public String hello() {
        return "Hello World";
    }
}