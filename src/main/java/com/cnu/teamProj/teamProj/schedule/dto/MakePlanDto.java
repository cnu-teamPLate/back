package com.cnu.teamProj.teamProj.schedule.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class MakePlanDto {
    private String userId;
    private String projId;
    private ZonedDateTime start;
    private ZonedDateTime end;
}
