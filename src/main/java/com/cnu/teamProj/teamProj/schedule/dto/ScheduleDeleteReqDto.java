package com.cnu.teamProj.teamProj.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(name = "스케줄 삭제 시 요청 값")
public class ScheduleDeleteReqDto {
    @Schema(description = "스케줄 아이디", type = "array", implementation = String.class, example = "[\"CSE00011_4\", \"CSE00011_3\"]")
    private List<String> scheId;
}
