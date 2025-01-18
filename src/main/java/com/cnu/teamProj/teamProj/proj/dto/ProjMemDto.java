package com.cnu.teamProj.teamProj.proj.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjMemDto {
    private String id;
    private String name;
    private String mail;
    private String phone;
    private String projId; //프로젝트에 인원 추가시 필요한 변수
}
