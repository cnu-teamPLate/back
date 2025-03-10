package com.cnu.teamProj.teamProj.schedule.entity;

import com.cnu.teamProj.teamProj.proj.entity.Project;
import com.cnu.teamProj.teamProj.schedule.dto.ScheduleUpdateDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
public class Schedule {
    @Id
    private String scheId;

    private ZonedDateTime date;
    private String scheName;
    private String place;
    private String category;
    private String detail;

    @ManyToOne
    @JoinColumn(name = "PROJ_ID", referencedColumnName = "projId")
    private Project projId;

    public Schedule(String scheId, ZonedDateTime date, String scheName, String place, String category, String detail, Project projId) {
        this.scheId = scheId;
        this.date = date;
        this.scheName = scheName;
        this.place = place;
        this.category = category;
        this.detail = detail;
        this.projId = projId;
    }

    public Schedule() {
    }

    public Schedule(ScheduleUpdateDto scheduleDto) {
        this.scheId = scheduleDto.getScheId();
        this.date = scheduleDto.getDate();
        this.scheName = scheduleDto.getScheName();
        this.place = scheduleDto.getPlace();
        this.category = scheduleDto.getCategory();
        this.detail = scheduleDto.getDetail();
    }
}
