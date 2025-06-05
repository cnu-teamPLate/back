package com.cnu.teamProj.teamProj.proj.dto;

import com.cnu.teamProj.teamProj.manage.entity.ClassInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjCreateDto {
    @Schema(description = "마감날짜", type = "string", example = "2025-01-15T00:02:27")
    private LocalDateTime date;
    @Schema(description = "목표", example = "MVP 완성")
    private String goal;
    @Schema(description = "프로젝트 명", example = "다같이몰(Mall)")
    private String projName;
    @Schema(description = "깃헙 리포지토리", example = "https://github.com/")
    private String github;
    @Schema(description = "수업 코드", example = "CSE4070-01")
    private String classId;
    @Schema(description = "팀 이름", example = "우리동네 팀")
    private String teamName;
    @Schema(description = "팀원들의 학번 리스트", example = "[\"01111111\", \"20210054\", \"20251239\"]")
    private List<String> members;
}
