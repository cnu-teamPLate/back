package com.cnu.teamProj.teamProj.schedule.entity;

import com.cnu.teamProj.teamProj.schedule.dto.TaskUpdateDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
public class Task {
    @Id
    private int taskId;
    private String id; //유저 아이디
    private String projId;
    private String role;
    private String detail;
    private ZonedDateTime date;
    private int level;
    private String cate;
    private int checkBox;

    public Task(TaskUpdateDto dto) {
        this.taskId = dto.getTaskId();
        this.id = dto.getId();
        this.projId = dto.getProjId();
        this.role = dto.getRole();
        this.detail = dto.getDetail();
        this.date = dto.getDate();
        this.level = dto.getLevel();
        this.cate = dto.getCate();
        this.checkBox = dto.getCheckBox();
    }
}
