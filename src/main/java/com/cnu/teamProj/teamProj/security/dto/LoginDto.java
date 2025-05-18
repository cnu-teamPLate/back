package com.cnu.teamProj.teamProj.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginDto {
    @Schema(description = "학번", example = "20211079")
    private String id;

    @Schema(description = "비밀번호", example = "1234")
    private String pwd;
}
