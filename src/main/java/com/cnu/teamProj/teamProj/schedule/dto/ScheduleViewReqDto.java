package com.cnu.teamProj.teamProj.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(name = "WeeklyRequestDto", description = "주간 일정 조회 요청 파라미터")
public class ScheduleViewReqDto {
    @Schema(description = "프로젝트 아이디<br/>(⚠️프로젝트 아이디 값이 없다면 학번 필수)", example = "cse00001", nullable = true)
    private String projId;
    @Schema(description = "학번<br/>(⚠️학번 값이 없다면 학번 필수)", example = "01111111", nullable = true)
    private String userId;
    @Schema(description = "기준 날짜", example = "2025-01-01T00:02:27")
    private LocalDateTime standardDate;
    @Schema(description = "🤝회의 = meeting<br/>📜과제 = task<br/>📅일정 = plan<br/>⚠️cate 값은 ,로 구분되며 사이에 공백이 있으면 안된다",
            example = "meeting,task", nullable = true)
    private String cate;
}
