package com.cnu.teamProj.teamProj.proj.dto;

import com.cnu.teamProj.teamProj.manage.entity.ClassInfo;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjCreateDto {
    private ZonedDateTime date;
    private String goal;
    private String projName;
    private String github;
    private String classId;
    private String teamName;
}
