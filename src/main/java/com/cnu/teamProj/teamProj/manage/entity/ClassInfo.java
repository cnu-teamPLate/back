package com.cnu.teamProj.teamProj.manage.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassInfo {
    @Id
    private String classId;
    private String className;
    private String professor;
    private int projCnt;
}
