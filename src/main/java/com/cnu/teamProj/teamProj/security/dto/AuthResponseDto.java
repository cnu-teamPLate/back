package com.cnu.teamProj.teamProj.security.dto;

import lombok.Data;

@Data
public class AuthResponseDto {
    private String accessToken;
    private String tokenType = "Bearer ";
    private String userId;
    private String name;

    public AuthResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }

    public AuthResponseDto(String accessToken, String userId, String name) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.name = name;
    }
}
