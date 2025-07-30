package com.cnu.teamProj.teamProj.task.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
public class TaskUpdateRequest {
    @Schema(description = "과제 관련 설명", example = "소셜 로그인은 카카오, 네이버 api 사용하기")
    private String description;
    @Schema(description = "수정한 사람 아이디", example = "01111111", type = "string")
    private String assigneeId;
}

