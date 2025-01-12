package com.cnu.teamProj.teamProj.schedule.entity;

import com.cnu.teamProj.teamProj.proj.entity.Project;
import com.cnu.teamProj.teamProj.security.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Table(name = "make_plan")
public class MakePlanEntity {
    @Id
    private int id;

    private ZonedDateTime start;
    private ZonedDateTime end;
    private String userName;

    @ManyToOne
    @JoinColumn(name = "USER_ID", referencedColumnName = "id")
    private User userId;

    @ManyToOne
    @JoinColumn(name = "PROJ_ID", referencedColumnName = "projId")
    private Project projId;


}
