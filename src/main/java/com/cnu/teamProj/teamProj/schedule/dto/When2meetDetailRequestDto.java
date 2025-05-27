package com.cnu.teamProj.teamProj.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class When2meetDetailRequestDto {
    @Schema(description = "웬투밋 폼 아이디", example = "1")
    private int when2meetId;
    @Schema(description = "각 유저가 선택한 값 리스트")
    private List<When2meetDetailDto> details;
}
