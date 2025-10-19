package com.cnu.teamProj.teamProj.security;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.List;

public class SecurityConstants {
    public static final long JWT_EXPIRATION = 70000;//

    @Value("${jwt-secret}")
    public static String JWT_SECRET;
    public static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(JWT_SECRET.getBytes());
    public static final List<String> PUBLIC_ENDPOINTS = List.of( //모든 사용자가 접근 가능한 url
            "/auth/login",
            "/auth/register",
            "/auth/send-password-mail",
            "/error",
            "/test/**",
            "/**"
    );
    public static final List<String> USER_ENDPOINTS = List.of( //가입한 유저만 접근 가능한 url
//            "/api/**",
//            "/file/**",
//            "/bucket/**",
//            "/teamProj/**",
//            "/projects/**",
//            "/schedule/**",
//            "/permission-check" //테스트용
    );

    public static final List<String> MANAGER_ENDPOINTS = List.of(
            "/teamProj/member/delete"
    );
}