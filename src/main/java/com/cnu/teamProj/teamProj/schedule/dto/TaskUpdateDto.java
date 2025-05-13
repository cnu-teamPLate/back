package com.cnu.teamProj.teamProj.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class TaskUpdateDto {
    private int taskId;
    private String id; //유저 아이디
    private String projId;
    private String role;
    private String detail;
    private ZonedDateTime date;
    private int level;
    private String cate;
    private int checkBox;
}
