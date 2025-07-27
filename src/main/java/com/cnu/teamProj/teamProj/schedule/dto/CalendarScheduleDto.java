package com.cnu.teamProj.teamProj.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "스케줄 정보 반환값")
public class CalendarScheduleDto {
    @Schema(description = "팀 전체 일정<br/>key 값 = 프로젝트 아이디, value 값 = 스케줄 정보")
    private Map<String, List<TeamScheduleDto>> teamSchedules; //팀 전체 일정 (예. 회의, 마감일정 등)
    @Schema(description = "개인 과제 일정<br/>key 값 = 프로젝트 아이디, value 값 = 과제 정보")
    private Map<String, List<TaskScheduleDto>> taskSchedules; //내 과제

}
