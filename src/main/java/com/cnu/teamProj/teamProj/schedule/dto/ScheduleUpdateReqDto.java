package com.cnu.teamProj.teamProj.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Schema(name = "스케줄 수정 시 요청 값")
public class ScheduleUpdateReqDto {
    @Schema(description = "스케줄 아이디", example = "cse00001_3")
    private String scheId;
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
    @Schema(description = "참여자들의 학번을 배열로 전달", example = "[\"20241121\", \"20251234\"]", type = "array", implementation = String.class)
    private List<String> participants;
}
