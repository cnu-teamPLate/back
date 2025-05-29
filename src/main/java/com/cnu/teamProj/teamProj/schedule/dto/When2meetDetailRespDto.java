package com.cnu.teamProj.teamProj.schedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class When2meetDetailRespDto {
    @Schema(description = "시작 시간", type = "string", example = "14:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime startTime;
    @Schema(description = "끝 시간", type = "string", example = "16:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime endTime;
    @Schema(description = "학번 정보", example = "20211079")
    private String userId;
    @Schema(description = "유저 이름", example = "홍길동")
    private String username;
}
