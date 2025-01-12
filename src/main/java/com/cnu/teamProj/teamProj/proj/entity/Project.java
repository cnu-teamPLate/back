package com.cnu.teamProj.teamProj.proj.entity;

import com.cnu.teamProj.teamProj.manage.entity.ClassInfo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Project {
    @Id
    private String projId;

    private String date;
    private String goal;
    private String projName;
    private String github;

    @ManyToOne
    @JoinColumn(name = "CLASS_ID" , referencedColumnName = "classId")
    private ClassInfo classId;
}
