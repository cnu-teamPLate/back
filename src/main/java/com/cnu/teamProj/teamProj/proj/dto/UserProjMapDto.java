package com.cnu.teamProj.teamProj.proj.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserProjMapDto {
    @Schema(description = "삭제할 유저의 학번 정보")
    private String userId;
    @Schema(description = "유저를 삭제할 프로젝트 아이디")
    private String projId;
}
