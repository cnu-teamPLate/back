package com.cnu.teamProj.teamProj.schedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class DateTimeRangeDto {
    @Schema(description = "같은 날짜의 가능한 시작 시간<br/>⚠️endDate와 날짜가 같아야 함", example = "2025-05-27T14:30:00", type="string")
    private LocalDateTime startDate;
    @Schema(description = "같은 날짜의 가능한 종료 시간<br/>⚠️startDate와 날짜가 같아야 함", example = "2025-05-27T16:30:00", type="string")
    private LocalDateTime endDate;
}
