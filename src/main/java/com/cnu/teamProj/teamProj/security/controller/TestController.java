package com.cnu.teamProj.teamProj.security.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class TestController {
    @GetMapping("/test")
    public String test() {
        return "테스트입니다";
    }

    @GetMapping("/permission-check")
    public String pmchk() {
        return "권한이 있는 사용자만 접근 가능합니다";
    }
}
