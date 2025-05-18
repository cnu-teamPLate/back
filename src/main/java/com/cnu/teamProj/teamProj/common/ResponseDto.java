package com.cnu.teamProj.teamProj.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ResponseDto {
    @Schema(description = "응답 상태 설명")
    private String message;
    @Schema(description = "상태 코드")
    private int code;
}
