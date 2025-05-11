package com.cnu.teamProj.teamProj.schedule.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MeetingLogId implements Serializable {
    private String scheId;
    private String projId;
}
