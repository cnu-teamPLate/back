package com.cnu.teamProj.teamProj.schedule.dto;

import com.cnu.teamProj.teamProj.schedule.entity.When2meet;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
public class When2meetRequestDto {
    @Schema(description = "when2meet 제목", example = "전체 회의 날짜")
    private String title;
    @Schema(description = "프로젝트 아이디", example = "cse00001")
    private String projId;
    @Schema(description = "시작 시간", example = "09:00:00", type = "string")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private LocalTime startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(description = "종료 시간", example = "22:00:00", type = "string")
    private LocalTime endTime;
    @Schema(description = "생성자가 선택한 날짜 나열")
    private List<TimeRangeDto> dates;

    public When2meetRequestDto(When2meet when2meet) {
        this.title = when2meet.getTitle();
        this.projId = when2meet.getProjId().getProjId();
        this.startTime = when2meet.getStartTime();
        this.endTime = when2meet.getEndTime();
    }

    public When2meetRequestDto(When2meet when2meet, List<TimeRangeDto> dates) {
        this.title = when2meet.getTitle();
        this.projId = when2meet.getProjId().getProjId();
        this.startTime = when2meet.getStartTime();
        this.endTime = when2meet.getEndTime();
        this.dates = dates;
    }
}
