package com.cnu.teamProj.teamProj.schedule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class When2meetViewRespDto {
    @Schema(description = "웬투밋 폼 정보")
    private When2meetRequestDto form;
    @Schema(description = "유저가 선택한 값 정보<br/>key(additionalProp) = 날짜 정보가 담김, value = 해당 날짜에 가능하다고 표시한 유저 정보와 시간 정보")
    private Map<LocalDate, List<When2meetDetailRespDto>> details;
    @Schema(description = "타임존은 default 값으로 들어있음", example = "Asia/Seoul")
    private String timeZone = "Asia/Seoul";

    public When2meetViewRespDto(When2meetRequestDto form, Map<LocalDate, List<When2meetDetailRespDto>> details) {
        this.form = form;
        this.details = details;
    }
}
