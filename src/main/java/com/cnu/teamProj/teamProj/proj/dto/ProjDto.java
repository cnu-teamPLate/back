package com.cnu.teamProj.teamProj.proj.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ProjDto {
    private String classId; //수업 코드 워크플로우 테스트
    private String projName; //프로젝트 이름
    private String date; //마감일
    private String goal; //목표
    private String githubLink; //깃헙 링크
    private String teamName; //팀명
    private List<StudentInfoDto> teamones; //팀원 리스트

    public ProjDto(String classId, String projName) {
        this.classId = classId;
        this.projName = projName;
    }
}
