package com.cnu.teamProj.teamProj.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "간단한 유저 정보")
public class SimpleUserInfoDto {
    @Schema(description = "유저 이름", example = "안혜연")
    private String name;
    @Schema(description = "학번", example = "11111111")
    private String id;
}
