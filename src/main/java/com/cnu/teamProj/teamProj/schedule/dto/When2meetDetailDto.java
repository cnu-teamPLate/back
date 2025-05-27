package com.cnu.teamProj.teamProj.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class When2meetDetailDto {
    @Schema(description = "선택한 유저", example = "20211079")
    private String userId;
    @Schema(description = "유저명<br/>📍응답용이므로 요청시에는 필요 없음", example = "홍길동", nullable = true)
    private String username;
    @Schema(description = "유저가 선택한 날짜 리스트<br/>")
    private List<DateTimeRangeDto> dates;
}
