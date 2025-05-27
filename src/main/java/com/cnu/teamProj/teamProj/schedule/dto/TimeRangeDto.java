package com.cnu.teamProj.teamProj.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class TimeRangeDto {
    @Schema(description = "시작 날짜", example = "2025-05-01")
    private LocalDate startDate;
    @Schema(description = "끝 날짜", example = "2025-05-01")
    private LocalDate endDate;
}
