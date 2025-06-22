package com.cnu.teamProj.teamProj.schedule.entity;

import com.cnu.teamProj.teamProj.proj.entity.Project;
import com.cnu.teamProj.teamProj.security.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Entity
@Getter
@AllArgsConstructor
@IdClass(ParticipantsId.class)
public class Participants {
    @Id
    @ManyToOne
    @JoinColumn(name = "scheId")
    private Schedule scheId;

    @Id
    @ManyToOne
    @JoinColumn(name = "id")
    private User id;

    @Id
    @ManyToOne
    @JoinColumn(name = "projId")
    private Project projId;

    public Participants() {
    }
}
