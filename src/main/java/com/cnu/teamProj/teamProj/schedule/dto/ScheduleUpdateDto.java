package com.cnu.teamProj.teamProj.schedule.dto;

import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Data
public class ScheduleUpdateDto {
    private String scheId;
    private String projId;
    private ZonedDateTime date;
    private String scheName;
    private String place;
    private String category;
    private String detail;
    private Map<String, Integer> participants; //userId:변동여부 (0 -> 변동x, 1->추가, -1->삭제)
}
