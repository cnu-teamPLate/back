package com.cnu.teamProj.teamProj.schedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeRangeDto {
    @Schema(description = "시작 날짜", example = "2025-05-01")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-ss")
    private LocalDate startDate;
    @Schema(description = "끝 날짜", example = "2025-05-01")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-ss")
    private LocalDate endDate;
}
