package com.cnu.teamProj.teamProj.task.entity;

import com.cnu.teamProj.teamProj.schedule.dto.TaskUpdateDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Table(name = "task") // DB 테이블명과 일치시킴
public class Task {
    @Id
    @Column(name = "TASK_ID") // DB 컬럼명과 정확히 매핑
    private int taskId; // ⚠ 데이터베이스에서는 `int`이므로 타입을 맞춰야 함

    @Column(name = "id")
    private String id; // 유저 아이디

    @Column(name = "PROJ_ID")
    private String projId;

    @Column(name = "ROLE")
    private String role;

    @Column(name = "DETAIL")
    private String detail;

    @Column(name = "DATE")
    private ZonedDateTime date;

    @Column(name = "LEVEL")
    private int level;

    @Column(name = "CATE")
    private String cate;

    @Column(name = "CHECKBOX") // DB 컬럼명과 일치
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

    public Task() {

    }

}
