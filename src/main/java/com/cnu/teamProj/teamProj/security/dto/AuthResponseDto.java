package com.cnu.teamProj.teamProj.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AuthResponseDto {
    @Schema(description = "발급된 jwt 토큰")
    private String accessToken;
    @Schema(description = "토큰 종류")
    private String tokenType = "Bearer ";
    @Schema(description = "학번 정보")
    private String userId;
    @Schema(description = "유저 이름")
    private String name; //username

    public AuthResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }

    public AuthResponseDto(String accessToken, String userId, String name) {
        this.accessToken = accessToken;
        this.userId = userId;
        this.name = name;
    }
}
