package com.cnu.teamProj.teamProj.schedule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MeetingLogDto {
    private String scheId;
    private String projId;
    private String contents;
    private String fix;
}
