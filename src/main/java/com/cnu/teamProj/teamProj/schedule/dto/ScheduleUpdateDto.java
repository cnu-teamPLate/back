package com.cnu.teamProj.teamProj.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Data
public class ScheduleUpdateDto {
    @Schema(description = "스케줄 아이디", example = "cse00001_3")
    private String scheId;
    @Schema(description = "프로젝트 아이디", example = "cse00001")
    private String projId;
    @Schema(description = "스케줄 날짜", example = "2025-01-14T00:02:27")
    private LocalDateTime date;
    @Schema(description = "스케줄 이름", example = "비대면 회의")
    private String scheName;
    @Schema(description = "스케줄 장소", example = "구글 밋")
    private String place;
    @Schema(description = "스케줄 종류 (과제=task, 회의=meeting, 일정=plan)", example = "meeting")
    private String category;
    @Schema(description = "스케줄 설명", example = "역할 분담 해야함")
    private String detail;
    @Schema(description = "참여자들의 학번을 배열로 전달", example = " {\n" +
            "                        \"00000000\":-1,\n" +
            "                        \"01111111\":1\n" +
            "                    }")
    private Map<String, Integer> participants; //userId:변동여부 (0 -> 변동x, 1->추가, -1->삭제)
}
