package com.cnu.teamProj.teamProj.schedule.dto;

import com.cnu.teamProj.teamProj.task.entity.Task;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;


@Data
@AllArgsConstructor
public class TaskScheduleDto {

    private int taskId;
    private String projName;
    private String role;
    private String deadLine;

    public TaskScheduleDto(Task task) {
        this.taskId = task.getTaskId();
        this.role = task.getRole();
        this.deadLine = task.getDate().toString();
    }
}
