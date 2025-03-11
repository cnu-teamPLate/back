package com.cnu.teamProj.teamProj.proj.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.ZonedDateTime;
import java.util.List;

@Data
public class ProjUpdateDto {
    private String projectId; // 업데이트할 프로젝트 식별 ID
    private ZonedDateTime date; // 선택적 필드: 수정 가능
    private String goal; // 수정 가능
    private String projName; // 수정 가능
    private String github; // 수정 가능
    private String teamName; // 수정 가
}
