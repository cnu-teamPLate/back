package com.cnu.teamProj.teamProj.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegisterDto {
    @Pattern(regexp = "^[0-9]{8}$")
    @Schema(description = "학번", example = "20251236")
    private String id; //학번
    @Schema(description = "비밀번호(수정 안할거면 빈 값 or null로 보내기)", example = "1234")
    private String pwd;
    @Schema(description = "이름")
    private String name;
    @Email
    @Schema(description = "메일 정보(중복 불가)", example = "esybd0123@naver.com")
    private String email; //mail
    @Schema(description = "전화번호")
    private String phone;
}
