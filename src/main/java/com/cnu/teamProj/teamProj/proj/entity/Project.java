package com.cnu.teamProj.teamProj.proj.entity;

import com.cnu.teamProj.teamProj.manage.entity.ClassInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
public class Project {
    @Id
    private String projId;

    private ZonedDateTime date;
    private String goal;
    private String projName;
    private String github;
    private String teamName;

    @ManyToOne
    @JoinColumn(name = "CLASS_ID" , referencedColumnName = "classId")
    private ClassInfo classId;
}
