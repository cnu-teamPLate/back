package com.cnu.teamProj.teamProj.schedule.dto;

import com.cnu.teamProj.teamProj.schedule.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class TeamScheduleDto {
    private String scheduleName;
    private String projName;
    private String date;
    private String category;
    private String place;

    public TeamScheduleDto(Schedule schedule) {
        this.scheduleName = schedule.getScheName();
        this.date = schedule.getDate().toString();
        this.category = schedule.getCategory();
        this.place = schedule.getPlace();
    }
}
