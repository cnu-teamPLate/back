package com.cnu.teamProj.teamProj.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalendarScheduleDto {
    private Map<String, List<TeamScheduleDto>> teamSchedules; //팀 전체 일정 (예. 회의, 마감일정 등)
    private Map<String, List<TaskScheduleDto>> taskSchedules; //내 과제

}
