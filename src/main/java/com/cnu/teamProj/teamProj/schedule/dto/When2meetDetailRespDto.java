package com.cnu.teamProj.teamProj.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class When2meetDetailRespDto {
    private LocalTime startTime;
    private LocalTime endTime;
    private String userId;
    private String username;
}
