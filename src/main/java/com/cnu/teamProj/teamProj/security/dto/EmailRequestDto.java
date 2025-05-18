package com.cnu.teamProj.teamProj.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.Data;

@Data
public class EmailRequestDto {
    @Email
    @Schema(name = "email", description = "유저 메일 정보", example = "esybd02@naver.com")
    private String email;
}
