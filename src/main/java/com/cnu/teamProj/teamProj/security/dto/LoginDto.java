package com.cnu.teamProj.teamProj.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "로그인 및 비밀번호 변경 시 사용되는 dto")
@AllArgsConstructor
@NoArgsConstructor
public class LoginDto {
    @Schema(description = "학번", example = "20211079")
    private String id;

    @Schema(description = "비밀번호", example = "1234")
    private String pwd;
}
