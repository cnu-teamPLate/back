package com.cnu.teamProj.teamProj.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DateTimeRangeDto {
    @Schema(description = "같은 날짜의 가능한 시작 시간<br/>⚠️endDate와 날짜가 같아야 함", example = "2025-05-27T14:30:00")
    private LocalDateTime startDate;
    @Schema(description = "같은 날짜의 가능한 종료 시간<br/>⚠️startDate와 날짜가 같아야 함", example = "2025-05-27T16:30:00")
    private LocalDateTime endDate;
}
