package com.cnu.teamProj.teamProj.schedule.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "특정 프로젝트에 등록된 회의록 리스트")
public class MeetingListDto {
    @Schema(description = "스케줄 아이디")
    private String scheId;
    @Schema(description = "회의록 제목")
    private String title;
    @Schema(description = "회의 날짜")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;
}
