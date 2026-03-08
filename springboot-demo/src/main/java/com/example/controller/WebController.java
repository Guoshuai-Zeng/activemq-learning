package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Web 控制器
 * 提供 Web 页面
 */
@Controller
public class WebController {

    /**
     * 首页
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 消息测试页面
     */
    @GetMapping("/test")
    public String test() {
        return "test";
    }

    /**
     * 监控页面
     */
    @GetMapping("/monitor")
    public String monitor() {
        return "monitor";
    }
}
