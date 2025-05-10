package com.cnu.teamProj.teamProj.schedule.entity;

import com.cnu.teamProj.teamProj.proj.entity.Project;
import com.cnu.teamProj.teamProj.schedule.dto.MeetingLogDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@IdClass(MeetingLogId.class)
@AllArgsConstructor
@Getter
@Setter
public class MeetingLog {

    @Id
    @ManyToOne
    @JoinColumn(name = "scheId")
    private Schedule scheId;

    @Id
    @ManyToOne
    @JoinColumn(name="projId")
    private Project projId;

    private String contents;
    private String fix;

    public MeetingLog() {

    }
}
