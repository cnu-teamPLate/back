package com.cnu.teamProj.teamProj.task.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Getter
@Setter
@NoArgsConstructor
public class TaskUpdateRequest {
    @Schema(description = "과제 관련 설명", example = "소셜 로그인은 카카오, 네이버 api 사용하기")
    private String description;
    @Schema(description = "수정한 사람 아이디", example = "01111111", type = "string")
    private String assigneeId;
    @Schema(description = "업로드 날짜", example = "2025-05-27T14:30:00", type = "string")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime deadline;
}

