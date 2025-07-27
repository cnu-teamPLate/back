package com.cnu.teamProj.teamProj.schedule.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParticipantsId implements Serializable {
    private String scheId;
    private String projId;
    private String id;
}
