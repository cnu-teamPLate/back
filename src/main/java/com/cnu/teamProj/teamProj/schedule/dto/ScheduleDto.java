package com.cnu.teamProj.teamProj.schedule.dto;

import com.cnu.teamProj.teamProj.schedule.entity.Schedule;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ScheduleDto {
    private String scheId;
    private String projId;
    private ZonedDateTime date;
    private String scheName;
    private String place;
    private String category;
    private String detail;
    private List<String> participants;

    public ScheduleDto(Schedule schedule) {
        this.scheId = schedule.getScheId();
        this.projId = schedule.getProjId().getProjId();
        this.date = schedule.getDate();
        this.scheName = schedule.getScheName();
        this.place = schedule.getPlace();
        this.category = schedule.getCategory();
        this.detail = schedule.getDetail();
    }
}
