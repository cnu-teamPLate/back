package com.cnu.teamProj.teamProj.schedule.dto;

import com.cnu.teamProj.teamProj.schedule.entity.Schedule;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@Schema(name = "팀 스케줄 정보")
public class TeamScheduleDto {
    private String scheduleId;
    private String scheduleName;
    private String projName;
    private String date;
    private String category;
    private String place;

    public TeamScheduleDto(Schedule schedule) {
        this.scheduleId = schedule.getScheId();
        this.scheduleName = schedule.getScheName();
        this.date = schedule.getDate().toString();
        this.category = schedule.getCategory();
        this.place = schedule.getPlace();
    }
}
